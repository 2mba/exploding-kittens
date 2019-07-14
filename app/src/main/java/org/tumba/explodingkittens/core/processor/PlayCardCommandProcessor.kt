package org.tumba.explodingkittens.core.processor

import org.tumba.explodingkittens.core.*

class PlayCardCommand(
    val playerId: Long,
    val cardId: Long
) : GameCommand {

    override val id: String = CommandId.PLAY_CARD
}

class PlayCardCommandProcessor(
    private val playCardProcessors: List<PlayCardProcessor>
) : TypedGameCommandProcessor<PlayCardCommand>() {

    override val commandId: String = CommandId.PLAY_CARD

    override fun processTyped(command: PlayCardCommand, gameState: GameState, gameManager: GameManager) {
        gameManager.ensureStateThat(
            Is(IntermediateGameState.PlayCard::class.java) and { state -> state.playerId == command.playerId }
        )
        val player = gameManager.getPlayerById(command.playerId)
        val card = gameManager.getPlayerCard(player, command.cardId)

        playCardProcessors.forEach { it.process(player, card, gameState, gameManager) }
    }

    interface PlayCardProcessor {

        fun process(player: Player, card: Card, gameState: GameState, gameManager: GameManager)
    }
}

abstract class SingleCardPlayCardProcessor(
    private val cardTypeForProcess: CardType
) : PlayCardCommandProcessor.PlayCardProcessor {

    override fun process(player: Player, card: Card, gameState: GameState, gameManager: GameManager) {
        if (cardTypeForProcess == card.type) {
            processCard(player, card, gameState, gameManager)
        }
    }

    abstract fun processCard(player: Player, card: Card, gameState: GameState, gameManager: GameManager)
}

class SkipCardProcessor : SingleCardPlayCardProcessor(CardType.SKIP) {

    override fun processCard(player: Player, card: Card, gameState: GameState, gameManager: GameManager) {
    }
}

class AttackCardProcessor : SingleCardPlayCardProcessor(CardType.ATTACK) {

    override fun processCard(player: Player, card: Card, gameState: GameState, gameManager: GameManager) {
        val newState = IntermediateGameState.PlayCard(
            playerId = gameManager.nextPlayer().id,
            numberOfCardToTake = gameState.intermediateGameState.numberOfCardToTakeFromStack + 1
        )
        gameManager.setIntermediateState(newState)
    }
}