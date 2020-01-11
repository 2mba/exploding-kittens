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
        when (takeCards(gameState, gameManager)) {
            TakeCardResult.OK -> {
                setNextPlayerPlayCardState(gameManager)
            }
            TakeCardResult.DEFUSED -> {
                setInsertCardState(gameManager)
            }
            TakeCardResult.EXPLODED -> {
                setNextPlayerPlayCardState(gameManager)
            }
        }
    }

    private fun takeCards(gameState: GameState, gameManager: GameManager): TakeCardResult {
        val state = gameState.intermediateGameState as PlayCard
        repeat(state.numberOfCardToTakeFromStack) {
            when (val takeCardResult = takeCard(gameState, gameManager)) {
                TakeCardResult.DEFUSED,
                TakeCardResult.EXPLODED -> {
                    return takeCardResult
                }
            }
        }
        return TakeCardResult.OK
    }

    private fun takeCard(gameState: GameState, gameManager: GameManager): TakeCardResult {
        val card = gameState.stack.pop()
        return if (card.type == CardType.EXPLODE) {
            if (tryDefuse(gameManager)) {
                TakeCardResult.DEFUSED
            } else {
                explodePlayer(gameState, gameManager)
                TakeCardResult.EXPLODED
            }
        } else {
            gameManager.currentPlayer().hand.add(card)
            TakeCardResult.OK
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
    }

    private fun setInsertCardState(gameManager: GameManager) {
        val newState = InsertExplodeCardToStack(
            playerId = gameManager.currentPlayer().id,
            numberOfCardToTake = 1
        )
        gameManager.setIntermediateState(newState)
    }

    private fun setNextPlayerPlayCardState(gameManager: GameManager) {
        val newState = PlayCard(
            playerId = gameManager.nextPlayer().id,
            numberOfCardToTake = 1
        )
        gameManager.setIntermediateState(newState)
    }

    private enum class TakeCardResult {
        EXPLODED,
        DEFUSED,
        OK
    }
}