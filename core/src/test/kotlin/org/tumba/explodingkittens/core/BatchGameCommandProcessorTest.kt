package org.tumba.explodingkittens.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class BatchGameCommandProcessorTest {

    private lateinit var gameState: GameState
    private lateinit var gameManager: GameManager
    private lateinit var commands: List<GameCommand>
    private lateinit var processors: List<GameCommandProcessor>
    private lateinit var processor: BatchGameCommandProcessor

    @Before
    fun setup() {
        gameState = mockk()
        gameManager = mockk()
        commands = listOf(mockk(), mockk())
        commands.forEachIndexed { index, command ->
            every { command.id } returns index.toString()
        }

        processors = commands.map { command ->
            val processor = mockk<GameCommandProcessor>(relaxed = true)
            every { processor.commandId } answers { command.id }
            processor
        }
        processor = BatchGameCommandProcessor(processors)
    }

    @Test
    fun `process should call process only on matched commands`() {
        processor.process(commands[0], gameState, gameManager)

        verify {
            processors[0].process(commands[0], gameState, gameManager)
        }

        verify(exactly = 0) {
            processors[0].process(neq(commands[0]), any(), any())
        }

        verify(exactly = 0) {
            processors[1].process(any(), any(), any())
        }
    }
}