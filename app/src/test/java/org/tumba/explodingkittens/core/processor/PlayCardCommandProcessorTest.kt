package org.tumba.explodingkittens.core.processor

import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.tumba.explodingkittens.core.GameManager
import org.tumba.explodingkittens.core.GameState
import org.tumba.explodingkittens.core.IntermediateGameState
import org.tumba.explodingkittens.utils.GameStateFactory
import kotlin.random.Random

class PlayCardCommandProcessorTest {

    private lateinit var gameStateFactory: GameStateFactory
    private lateinit var gameState: GameState
    private lateinit var gameManager: GameManager
    private lateinit var command: PlayCardCommand
    private lateinit var processor: PlayCardCommandProcessor
    private lateinit var playCardProcessors: List<PlayCardCommandProcessor.PlayCardProcessor>

    @Before
    fun setup() {
        val random = Random(0)
        gameStateFactory = GameStateFactory(random)
        gameState = gameStateFactory.create(2)
        gameManager = GameManager(gameState)
        command = mockk()
        every { command.id } returns CommandId.PLAY_CARD
        every { command.playerId } returns gameState.players.first().id
        playCardProcessors = listOf(
            SkipCardProcessor(),
            AttackCardProcessor()
        )
        processor = PlayCardCommandProcessor(playCardProcessors)
    }

    @Test
    fun `should not throw when state is play card by player id in command`() {
        processor.process(command, gameState, gameManager)
    }

    @Test(expected = IllegalStateException::class)
    fun `should throw when game state play card by other player`() {
        gameState.intermediateGameState = IntermediateGameState.PlayCard(
            playerId = gameState.players.last().id,
            numberOfCardToTake = 1
        )
        processor.process(command, gameState, gameManager)
    }

    @Test(expected = IllegalStateException::class)
    fun `should throw when game state not play card`() {
        gameState.intermediateGameState = IntermediateGameState.TakeCard(
            playerId = gameState.players.last().id,
            numberOfCardToTakeFromStack = 1
        )
        processor.process(command, gameState, gameManager)
    }
}
