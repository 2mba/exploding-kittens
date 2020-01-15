package org.tumba.explodingkittens.console

import org.tumba.explodingkittens.core.Game
import org.tumba.explodingkittens.core.IntermediateGameState
import org.tumba.explodingkittens.core.Player
import org.tumba.explodingkittens.core.factory.GameFactory
import org.tumba.explodingkittens.core.factory.InitialGameStateFactory
import org.tumba.explodingkittens.core.factory.PlayerHandFactory
import org.tumba.explodingkittens.core.processor.StopPlayCardCommand
import kotlin.random.Random

fun main() {
    val game = createGame()
    game.eventListener = { println(it) }

    outerLoop@ while (true) {
        println("---> Current state = ${game.state}")
        when (val intermediateGameState = game.state.intermediateGameState) {
            is IntermediateGameState.PlayCard -> {
                game.executeCommand(StopPlayCardCommand(intermediateGameState.playerId))
            }
            /*is IntermediateGameState.TakeCard -> {

            }*/
            else -> {
                break@outerLoop
            }
        }
    }
}

private fun createGame(): Game {
    val random = Random(0)
    val playerHandFactory = PlayerHandFactory()
    val players = listOf(
        Player(0, "Player 0", true, playerHandFactory.createPlayerHand()),
        Player(1, "Player 1", true, playerHandFactory.createPlayerHand()),
        Player(2, "Player 2", true, playerHandFactory.createPlayerHand())
    )
    val gameFactory = GameFactory.create(
        gameStateFactory = InitialGameStateFactory(players, random)
    )
    return gameFactory.createGame()
}