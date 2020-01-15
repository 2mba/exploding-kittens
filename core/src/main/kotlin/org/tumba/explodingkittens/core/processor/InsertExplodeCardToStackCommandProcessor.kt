package org.tumba.explodingkittens.core.processor

import org.tumba.explodingkittens.core.*
import org.tumba.explodingkittens.core.IntermediateGameState.InsertExplodeCardToStack
import org.tumba.explodingkittens.core.IntermediateGameState.PlayCard

class InsertExplodeCardToStackCommand(
    val playerId: Long,
    val insertionIndex: Int,
    val showCardInsertionPlacement: Boolean
) : GameCommand {

    override val id: String = CommandId.INSERT_EXPLODE_CARD_TO_STACK
}

class InsertExplodeCardToStackCommandProcessor : TypedGameCommandProcessor<InsertExplodeCardToStackCommand>() {

    override val commandId: String = CommandId.INSERT_EXPLODE_CARD_TO_STACK

    override fun processTyped(
        command: InsertExplodeCardToStackCommand,
        gameState: GameState,
        gameManager: GameManager
    ) {
        gameManager.ensureStateThat(
            Is(InsertExplodeCardToStack::class.java) and { state -> state.playerId == command.playerId }
        )
        val intermediateGameState = gameState.intermediateGameState as InsertExplodeCardToStack
        if (command.insertionIndex < gameState.stack.size()) {
            gameState.stack.put(command.insertionIndex, intermediateGameState.explodeCard)
        } else {
            gameState.stack.put(gameState.stack.size(), intermediateGameState.explodeCard)
        }
        gameManager.event("${gameManager.currentPlayer().name} insert explode card to ${command.insertionIndex}")
        setNextPlayerPlayCardState(gameManager)
    }


    private fun setNextPlayerPlayCardState(gameManager: GameManager) {
        val newState = PlayCard(
            playerId = gameManager.nextPlayer().id,
            numberOfCardToTake = 1
        )
        gameManager.setIntermediateState(newState)
        gameManager.event("Next player turn")
    }

    private enum class TakeCardResult {
        EXPLODED,
        DEFUSED,
        OK
    }
}