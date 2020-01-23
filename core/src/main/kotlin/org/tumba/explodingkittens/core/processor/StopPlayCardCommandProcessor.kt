package org.tumba.explodingkittens.core.processor

import org.tumba.explodingkittens.core.*
import org.tumba.explodingkittens.core.IntermediateGameState.InsertExplodeCardToStack
import org.tumba.explodingkittens.core.IntermediateGameState.PlayCard

class StopPlayCardCommand(
    val playerId: Long
) : GameCommand {

    override val id: String = CommandId.STOP_PLAY_CARD
}

class StopPlayCardCommandProcessor : TypedGameCommandProcessor<StopPlayCardCommand>() {

    override val commandId: String = CommandId.STOP_PLAY_CARD

    override fun processTyped(
        command: StopPlayCardCommand,
        gameState: GameState,
        gameManager: GameManager
    ) {
        gameManager.ensureStateThat(
            Is(PlayCard::class.java) and { state -> state.playerId == command.playerId }
        )
        gameManager.event("${gameManager.currentPlayer().name} stop playing card and take cards")
        when (val result = takeCards(gameState, gameManager)) {
            is TakeCardResult.Ok -> {
                setNextPlayerPlayCardState(gameManager)
            }
            is TakeCardResult.Defused -> {
                setInsertCardState(gameManager, result.explodeCard)
            }
            is TakeCardResult.Exploded -> {
                if (isAliveOnlyOnePlayer(gameState)) {
                    setWinState(gameManager, gameState)
                } else {
                    setNextPlayerPlayCardState(gameManager)
                }
            }
        }
    }

    private fun takeCards(gameState: GameState, gameManager: GameManager): TakeCardResult {
        val state = gameState.intermediateGameState as PlayCard
        repeat(state.numberOfCardToTakeFromStack) {
            when (val takeCardResult = takeCard(gameState, gameManager)) {
                is TakeCardResult.Defused,
                is TakeCardResult.Exploded -> {
                    return takeCardResult
                }
            }
        }
        return TakeCardResult.Ok
    }

    private fun takeCard(gameState: GameState, gameManager: GameManager): TakeCardResult {
        val card = gameState.stack.pop()
        gameManager.event("${gameManager.currentPlayer().name} has taken card $card")
        return if (card.type == CardType.EXPLODE) {
            if (tryDefuse(gameManager)) {
                gameManager.event("${gameManager.currentPlayer().name} has defused exploding kitten")
                TakeCardResult.Defused(card)
            } else {
                explodePlayer(gameState, gameManager)
                TakeCardResult.Exploded
            }
        } else {
            gameManager.currentPlayer().hand.add(card)
            TakeCardResult.Ok
        }
    }

    private fun tryDefuse(gameManager: GameManager): Boolean {
        val defuseCard = gameManager.getCardOfPlayer(gameManager.currentPlayer(), CardType.DEFUSE)
        if (defuseCard != null) {
            gameManager.removeCardOfPlayer(gameManager.currentPlayer(), defuseCard)
        }
        return defuseCard != null
    }

    private fun explodePlayer(gameState: GameState, gameManager: GameManager) {
        val deadPlayer = gameManager.currentPlayer().copy(isAlive = false)
        val indexOfCurrentPlayer = gameState.players.indexOf(gameManager.currentPlayer())
        gameState.players[indexOfCurrentPlayer] = deadPlayer
        gameManager.event("${gameManager.currentPlayer().name} has been exploded")
    }

    private fun setInsertCardState(gameManager: GameManager, explodeCard: Card) {
        val newState = InsertExplodeCardToStack(
            playerId = gameManager.currentPlayer().id,
            numberOfCardToTake = 1,
            explodeCard = explodeCard
        )
        gameManager.setIntermediateState(newState)
    }

    private fun isAliveOnlyOnePlayer(gameState: GameState): Boolean {
        return gameState.players.count { it.isAlive } == 1
    }

    private fun setNextPlayerPlayCardState(gameManager: GameManager) {
        val newState = PlayCard(
            playerId = gameManager.nextPlayer().id,
            numberOfCardToTake = 1
        )
        gameManager.setIntermediateState(newState)
        gameManager.event("Next player turn")
    }

    private fun setWinState(gameManager: GameManager, gameState: GameState) {
        val winnerPlayerId = gameState.players.first { it.isAlive }.id
        val newState = IntermediateGameState.Win(winnerPlayerId)
        gameManager.setIntermediateState(newState)
        gameManager.event("${gameManager.getPlayerById(winnerPlayerId).name} is winner!!!")
    }

    private sealed class TakeCardResult {
        object Exploded : TakeCardResult()
        class Defused(val explodeCard: Card): TakeCardResult()
        object Ok : TakeCardResult()
    }
}