package org.tumba.explodingkittens.core.processor

import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.tumba.explodingkittens.core.CardType
import org.tumba.explodingkittens.core.GameManager
import org.tumba.explodingkittens.core.GameState
import org.tumba.explodingkittens.core.IntermediateGameState
import org.tumba.explodingkittens.utils.CardFactory
import org.tumba.explodingkittens.utils.GameStateFactory
import org.tumba.explodingkittens.utils.SimpleCardStackFactory
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
        gameManager = GameManager(gameState)
        processor = StopPlayCardCommandProcessor()
        cardFactory = CardFactory(random)
    }

    @Test
    fun `should get card and set turn to next player`() {
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
        gameManager = GameManager(gameState)

        val player = gameState.players.first()
        val playerHandSize = player.hand.size()
        val nextPlayer = gameState.players[1]
        gameState.intermediateGameState = IntermediateGameState.PlayCard(
            playerId = player.id,
            numberOfCardToTake = 2
        )
        command = StopPlayCardCommand(player.id)
        processor.process(command, gameState, gameManager)

        gameState.intermediateGameState `should equal` IntermediateGameState.PlayCard(
            playerId = nextPlayer.id,
            numberOfCardToTake = 1
        )
        gameState.stack.size() `should equal` 0
        gameManager.getPlayerById(player.id).hand.size() `should equal`  playerHandSize + 2
    }

    @Test(expected = IllegalStateException::class)
    fun `should throw because of out of turn`() {
        val player = gameState.players.first()
        gameState.intermediateGameState = IntermediateGameState.TakeCard(
            playerId = player.id,
            numberOfCardToTakeFromStack = 1
        )
        command = StopPlayCardCommand(player.id)
        processor.process(command, gameState, gameManager)
    }
}