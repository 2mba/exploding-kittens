package org.tumba.explodingkittens.core.processor

import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.tumba.explodingkittens.core.*
import org.tumba.explodingkittens.utils.*
import kotlin.random.Random

class PlayCardCommandProcessorTest {

    private lateinit var random: Random
    private lateinit var gameStateFactory: GameStateFactory
    private lateinit var gameState: GameState
    private lateinit var gameManager: GameManager
    private lateinit var command: PlayCardCommand
    private lateinit var processor: PlayCardCommandProcessor
    private lateinit var playCardProcessors: List<PlayCardCommandProcessor.PlayCardProcessor>
    private lateinit var cardFactory: CardFactory

    @Before
    fun setup() {
        random = Random(0)
        cardFactory = CardFactory(random)
        gameStateFactory = GameStateFactory(random)
        gameState = gameStateFactory.create(2)
        gameManager = GameManager(gameState)
        command = PlayCardCommand(
            playerId = gameState.players.first().id,
            cardId = gameState.players.first().hand.getAll().first().id
        )
        playCardProcessors = listOf(
            SkipCardProcessor(),
            AttackCardProcessor()
        )
        processor = PlayCardCommandProcessor(playCardProcessors)
    }

    @Test
    fun `should not throw when state is play card by player id in command`() {
        gameState.intermediateGameState = IntermediateGameState.PlayCard(
            playerId = gameState.players.first().id,
            numberOfCardToTake = 1
        )
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

    @Test
    fun `should set turn to next player and when played attack card`() {
        gameStateFactory = GameStateFactory(random).apply {
            playerFactory = PlayerFactoryImpl(random).apply {
                playerHandFactory = OneHandPlayerHandFactory(
                    hand = PlayerHandImpl(listOf(cardFactory.createOfType(CardType.ATTACK)))
                )
            }
        }
        gameState = gameStateFactory.create(playersCount = 3)
        gameManager = GameManager(gameState)
        val currentPlayer = gameState.players.first()
        command = PlayCardCommand(
            playerId = currentPlayer.id,
            cardId = currentPlayer.hand.getAll().first().id
        )
        gameState.intermediateGameState = IntermediateGameState.PlayCard(
            playerId = currentPlayer.id,
            numberOfCardToTake = 1
        )

        processor.process(command, gameState, gameManager)

        gameState.intermediateGameState `should equal` IntermediateGameState.PlayCard(
            playerId = gameState.players[1].id,
            numberOfCardToTake = 2
        )
    }
}
