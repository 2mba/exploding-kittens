package org.tumba.explodingkittens.console

import org.tumba.explodingkittens.core.Game
import org.tumba.explodingkittens.core.IntermediateGameState
import org.tumba.explodingkittens.core.Player
import org.tumba.explodingkittens.core.factory.GameFactory
import org.tumba.explodingkittens.core.factory.InitialGameStateFactory
import org.tumba.explodingkittens.core.factory.PlayerHandFactory
import org.tumba.explodingkittens.core.processor.InsertExplodeCardToStackCommand
import org.tumba.explodingkittens.core.processor.StopPlayCardCommand
import kotlin.random.Random


fun main() {
    val random = Random(System.currentTimeMillis())
    val game = createGame(random)
    game.eventListener = { println(it) }

    outerLoop@ while (true) {
        println("---> Current state = ${game.state}")
        when (val intermediateGameState = game.state.intermediateGameState) {
            is IntermediateGameState.PlayCard -> {
                game.executeCommand(StopPlayCardCommand(intermediateGameState.playerId))
            }
            is IntermediateGameState.InsertExplodeCardToStack -> {
                game.executeCommand(
                    InsertExplodeCardToStackCommand(
                        playerId = intermediateGameState.playerId,
                        insertionIndex = if (game.state.stack.size() == 0) {
                            0
                        } else {
                            random.nextInt(game.state.stack.size())
                        },
                        showCardInsertionPlacement = true
                    )
                )
            }
            else -> {
                break@outerLoop
            }
        }
    }
}

private fun createGame(random: Random): Game {
    val playerHandFactory = PlayerHandFactory()
    val players = listOf(
        Player(0, "Баира", true, playerHandFactory.createPlayerHand()),
        Player(1, "Павел", true, playerHandFactory.createPlayerHand()),
        Player(2, "Олечка", true, playerHandFactory.createPlayerHand())
    )
    val gameFactory = GameFactory.create(
        gameStateFactory = InitialGameStateFactory(players, random)
    )
    return gameFactory.createGame()
}