package org.tumba.explodingkittens.core.processor

import org.tumba.explodingkittens.core.*

class PlayCardCommand(
    val playerId: Long,
    val cardId: Long
) : GameCommand {

    override val id: String = CommandId.PLAY_CARD
}

class PlayCardCommandProcessor : TypedGameCommandProcessor<PlayCardCommand>() {

    override val commandId: String = CommandId.PLAY_CARD

    override fun processTyped(command: PlayCardCommand, gameState: GameState, gameManager: GameManager) {
        gameManager.ensureStateThat(
            EqualTo(IntermediateGameState.PlayCard(command.playerId))
        )
    }
}