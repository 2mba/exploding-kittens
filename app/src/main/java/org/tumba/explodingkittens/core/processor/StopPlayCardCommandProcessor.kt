package org.tumba.explodingkittens.core.processor

import org.tumba.explodingkittens.core.*
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
        val state = gameState.intermediateGameState as PlayCard
        repeat(state.numberOfCardToTakeFromStack) {
            val card = gameState.stack.pop()
            if (card.type == CardType.EXPLODE) {
                handleExplodeCard(gameState, gameManager)
            } else {
                gameManager.currentPlayer().hand.add(card)
            }
        }
        val newState = PlayCard(
            playerId = gameManager.nextPlayer().id,
            numberOfCardToTake = 1
        )
        gameManager.setIntermediateState(newState)
    }

    private fun handleExplodeCard(
        gameState: GameState,
        gameManager: GameManager
    ) {
        val defuseCard = gameManager.getCardOfPlayer(gameManager.currentPlayer(), CardType.DEFUSE)
        if (defuseCard == null) {
            explodePlayer(gameState, gameManager)
        } else {
            gameManager.removeCardOfPlayer(gameManager.currentPlayer(), defuseCard)
        }
    }

    private fun explodePlayer(
        gameState: GameState,
        gameManager: GameManager
    ) {
        val deadPlayer = gameManager.currentPlayer().copy(isAlive = false)
        val indexOfCurrentPlayer = gameState.players.indexOf(gameManager.currentPlayer())
        gameState.players[indexOfCurrentPlayer] = deadPlayer
    }
}