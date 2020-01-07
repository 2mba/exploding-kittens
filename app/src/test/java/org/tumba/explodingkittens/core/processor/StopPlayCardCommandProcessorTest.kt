package org.tumba.explodingkittens.core.processor

import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.tumba.explodingkittens.core.CardType
import org.tumba.explodingkittens.core.GameManager
import org.tumba.explodingkittens.core.GameState
import org.tumba.explodingkittens.core.IntermediateGameState
import org.tumba.explodingkittens.utils.*
import kotlin.random.Random

class StopPlayCardCommandProcessorTest {

    private lateinit var gameStateFactory: GameStateFactory
    private lateinit var gameState: GameState
    private lateinit var gameManager: GameManager
    private lateinit var processor: StopPlayCardCommandProcessor
    private lateinit var random: Random
    private lateinit var cardFactory: CardFactory
    private lateinit var command: StopPlayCardCommand

    @Before
    fun setup() {
        random = Random(0)
        gameStateFactory = GameStateFactory(random)
        gameState = gameStateFactory.create(playersCount = 3)
        createGameManger()
        processor = StopPlayCardCommandProcessor()
        cardFactory = CardFactory(random)
    }

    @Test
    fun `should get card and set turn to next player`() {
        // Arrange
        gameStateFactory = GameStateFactory(random).apply {
            cardStackFactory = SimpleCardStackFactory(
                cards = listOf(
                    cardFactory.createOfType(CardType.NONE),
                    cardFactory.createOfType(CardType.NONE)
                ),
                random = random
            )
        }
        gameState = gameStateFactory.create(playersCount = 3)
        createGameManger()

        val player = gameState.players.first()
        val playerHandSize = player.hand.size()
        val nextPlayer = gameState.players[1]
        gameState.intermediateGameState = IntermediateGameState.PlayCard(
            playerId = player.id,
            numberOfCardToTake = 2
        )
        command = StopPlayCardCommand(player.id)

        // Act
        processCommand()

        // Assert
        gameState.intermediateGameState `should equal` IntermediateGameState.PlayCard(
            playerId = nextPlayer.id,
            numberOfCardToTake = 1
        )
        gameState.stack.size() `should equal` 0
        gameManager.getPlayerById(player.id).isAlive `should equal` true
        gameManager.getPlayerById(player.id).hand.size() `should equal` playerHandSize + 2
    }

    @Test
    fun `should explode and die when get explode card and if no defuse card`() {
        // Arrange
        gameStateFactory = GameStateFactory(random).apply {
            cardStackFactory = SimpleCardStackFactory(
                cards = listOf(
                    cardFactory.createOfType(CardType.EXPLODE),
                    cardFactory.createOfType(CardType.NONE)
                ),
                random = random
            )
            playerFactory = PlayerFactoryImpl(random).apply {
                playerHandFactory = SimplePlayerHandFactory(
                    cards = listOf(cardFactory.createOfType(CardType.NONE))
                )
            }
        }
        gameState = gameStateFactory.create(playersCount = 3)
        createGameManger()

        val player = gameState.players.first()
        val nextPlayer = gameState.players[1]
        gameState.intermediateGameState = IntermediateGameState.PlayCard(
            playerId = player.id,
            numberOfCardToTake = 2
        )
        command = StopPlayCardCommand(player.id)

        // Act
        processCommand()

        // Assert
        gameState.intermediateGameState `should equal` IntermediateGameState.PlayCard(
            playerId = nextPlayer.id,
            numberOfCardToTake = 1
        )
        gameState.stack.size() `should equal` 1
        gameManager.getPlayerById(player.id).isAlive `should equal` false
        gameManager.getPlayerById(nextPlayer.id).isAlive `should equal` true
    }

    @Test
    fun `should not explode when get explode card and defuse`() {
        // Arrange
        gameStateFactory = GameStateFactory(random).apply {
            cardStackFactory = SimpleCardStackFactory(
                cards = listOf(
                    cardFactory.createOfType(CardType.EXPLODE),
                    cardFactory.createOfType(CardType.NONE),
                    cardFactory.createOfType(CardType.NONE)
                ),
                random = random
            )
            playerFactory = PlayerFactoryImpl(random).apply {
                playerHandFactory = SimplePlayerHandFactory(
                    cards = listOf(
                        cardFactory.createOfType(CardType.NONE),
                        cardFactory.createOfType(CardType.DEFUSE),
                        cardFactory.createOfType(CardType.NONE)
                    )
                )
            }
        }
        gameState = gameStateFactory.create(playersCount = 3)
        createGameManger()

        val player = gameState.players.first()
        gameState.intermediateGameState = IntermediateGameState.PlayCard(
            playerId = player.id,
            numberOfCardToTake = 2
        )
        command = StopPlayCardCommand(player.id)

        // Act
        processCommand()

        // Assert
        gameState.intermediateGameState `should equal` IntermediateGameState.InsertExplodeCardToStack(
            playerId = player.id,
            numberOfCardToTake = 1
        )
        gameState.stack.size() `should equal` 2
        val currentPlayer = gameManager.getPlayerById(player.id)
        currentPlayer.hand.size() `should equal` 2
        currentPlayer.hand.getAll().firstOrNull { it.type == CardType.DEFUSE } `should equal` null
        currentPlayer.isAlive `should equal` true
    }
    
    @Test(expected = IllegalStateException::class)
    fun `should throw because of out of turn`() {
        val player = gameState.players.first()
        gameState.intermediateGameState = IntermediateGameState.TakeCard(
            playerId = player.id,
            numberOfCardToTakeFromStack = 1
        )
        command = StopPlayCardCommand(player.id)
        processCommand()
    }

    private fun processCommand() {
        processor.process(command, gameState, gameManager)
    }
    
    private fun createGameManger() {
        gameManager = GameManager(gameState)
    }
}