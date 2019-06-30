package org.tumba.explodingkittens.utils

import org.tumba.explodingkittens.core.CardDropImpl
import org.tumba.explodingkittens.core.GameState
import org.tumba.explodingkittens.core.IntermediateGameState
import kotlin.random.Random

class GameStateFactory(random: Random) {

    var playerFactory: PlayerFactory = RandomPlayerFactory(random)
    var cardStackFactory: CardStackFactory = CardStackFactoryImpl(random)

    fun create(playersCount: Int): GameState {
        val players = generateSequence { playerFactory.create() }
            .take(playersCount)
            .toList()
        val stack = cardStackFactory.create(playersCount)
        val drop = CardDropImpl()

        return GameState(
            players,
            stack,
            drop,
            IntermediateGameState.PlayCard(
                playerId = players.first().id,
                numberOfCardToTake = 1
            )
        )
    }
}