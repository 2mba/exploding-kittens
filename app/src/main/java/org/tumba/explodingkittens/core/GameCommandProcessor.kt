package org.tumba.explodingkittens.core

interface GameCommandProcessor {

    val commandId: String

    fun process(command: GameCommand, gameState: GameState, gameManager: GameManager)
}

interface GameCommand {
    val id: String
}

class BatchGameCommandProcessor(
    private val processors: List<GameCommandProcessor>
) : GameCommandProcessor {

    override val commandId: String = ""

    override fun process(command: GameCommand, gameState: GameState, gameManager: GameManager) {
        processors
            .asSequence()
            .filter { it.commandId == command.id }
            .forEach { it.process(command, gameState, gameManager) }
    }
}

abstract class TypedGameCommandProcessor<T: GameCommand>: GameCommandProcessor {

    override fun process(command: GameCommand, gameState: GameState, gameManager: GameManager) {
        val typedCommand = (command as? T) ?: throw IllegalStateException("Incorrect command type")
        processTyped(typedCommand, gameState, gameManager)
    }

    abstract fun processTyped(command: T, gameState: GameState, gameManager: GameManager)
}