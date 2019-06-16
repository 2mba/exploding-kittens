package org.tumba.explodingkittens.core.processor

import org.tumba.explodingkittens.core.*

class SkipPlayCardCommand(
    val playerId: Long
) : GameCommand {

    override val id: String = CommandId.SKIP_PLAY_CARD
}

class SkipPlayCardCommandProcessor : TypedGameCommandProcessor<SkipPlayCardCommand>() {

    override val commandId: String = CommandId.SKIP_PLAY_CARD

    override fun processTyped(command: SkipPlayCardCommand, gameState: GameState, gameManager: GameManager) {
        gameManager.ensureStateThat(
            EqualTo(IntermediateGameState.PlayCard(command.playerId))
        )
        gameManager.setIntermediateState(IntermediateGameState.TakeCard(command.playerId))
    }
}