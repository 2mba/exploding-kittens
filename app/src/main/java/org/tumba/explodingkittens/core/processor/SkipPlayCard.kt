package org.tumba.explodingkittens.core.processor

import org.tumba.explodingkittens.core.*

class StopPlayCardCommand(
    val playerId: Long
) : GameCommand {

    override val id: String = CommandId.STOP_PLAY_CARD
}

class StopPlayCardCommandProcessor : TypedGameCommandProcessor<StopPlayCardCommand>() {

    override val commandId: String = CommandId.STOP_PLAY_CARD

    override fun processTyped(command: StopPlayCardCommand, gameState: GameState, gameManager: GameManager) {
        gameManager.ensureStateThat(
            Is(IntermediateGameState.PlayCard::class.java) and { state -> state.playerId == command.playerId }
        )
        val state = gameState.intermediateGameState as IntermediateGameState.PlayCard
        repeat(state.numberOfCardToTakeFromStack) {
            val card = gameState.stack.pop()
            if (card.type != CardType.EXPLODE) {
                gameManager.currentPlayer().hand.add(card)
            } else {
                TODO("Explode!!!")
            }
        }
        val newState = IntermediateGameState.PlayCard(
            playerId = gameManager.nextPlayer().id,
            numberOfCardToTake = 1
        )
        gameManager.setIntermediateState(newState)
    }

}