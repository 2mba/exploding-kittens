package org.tumba.explodingkittens.core.processor

import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should not contain`
import org.junit.Before
import org.junit.Test
import org.tumba.explodingkittens.core.CardType
import org.tumba.explodingkittens.core.GameManager
import org.tumba.explodingkittens.core.GameState
import org.tumba.explodingkittens.core.IntermediateGameState
import org.tumba.explodingkittens.core.utils.CardFactory
import org.tumba.explodingkittens.core.utils.GameStateFactory
import kotlin.random.Random

class InsertExplodeCardToStackCommandProcessorTest {

    private lateinit var random: Random
    private lateinit var gameStateFactory: GameStateFactory
    private lateinit var gameState: GameState
    private lateinit var gameManager: GameManager
    private lateinit var command: InsertExplodeCardToStackCommand
    private lateinit var processor: InsertExplodeCardToStackCommandProcessor
    private lateinit var cardFactory: CardFactory

    @Before
    fun setup() {
        random = Random(0)
        cardFactory = CardFactory(random)
        gameStateFactory = GameStateFactory(random)
        gameState = gameStateFactory.create(4)
        gameManager = GameManager(gameState)
        processor = InsertExplodeCardToStackCommandProcessor()
    }

    @Test
    fun `should insert card to top of stack and set turn to next player`() {
        // Arrange
        createGameManger()
        val explodeCard = cardFactory.createOfType(CardType.EXPLODE)
        val player = gameState.players.first()
        val nextPlayer = gameState.players[1]
        gameState.intermediateGameState = IntermediateGameState.InsertExplodeCardToStack(
            playerId = player.id,
            numberOfCardToTake = 0,
            explodeCard = explodeCard
        )
        command = InsertExplodeCardToStackCommand(
            playerId = player.id,
            insertionIndex = 0,
            showCardInsertionPlacement = true
        )

        // Act
        processCommand()

        // Assert
        gameState.intermediateGameState `should equal` IntermediateGameState.PlayCard(
            playerId = nextPlayer.id,
            numberOfCardToTake = 1
        )
        gameState.stack.pop() `should equal` explodeCard
        player.hand.getAll() `should not contain` explodeCard
    }

    @Test
    fun `should insert card to top of stack and set turn to current player`() {
        // Arrange
        createGameManger()
        val explodeCard = cardFactory.createOfType(CardType.EXPLODE)
        val player = gameState.players.first()
        val nextPlayer = gameState.players[1]
        gameState.intermediateGameState = IntermediateGameState.InsertExplodeCardToStack(
            playerId = player.id,
            numberOfCardToTake = 1,
            explodeCard = explodeCard
        )
        command = InsertExplodeCardToStackCommand(
            playerId = player.id,
            insertionIndex = 0,
            showCardInsertionPlacement = true
        )

        // Act
        processCommand()

        // Assert
        gameState.intermediateGameState `should equal` IntermediateGameState.PlayCard(
            playerId = player.id,
            numberOfCardToTake = 1
        )
        gameState.stack.pop() `should equal` explodeCard
    }

    private fun processCommand() {
        processor.process(command, gameState, gameManager)
    }

    private fun createGameManger() {
        gameManager = GameManager(gameState)
    }
}