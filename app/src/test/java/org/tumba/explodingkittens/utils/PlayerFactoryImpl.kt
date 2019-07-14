package org.tumba.explodingkittens.utils

import org.tumba.explodingkittens.core.Player
import kotlin.random.Random

interface PlayerFactory {

    fun create(): Player
}


class PlayerFactoryImpl(random: Random) : PlayerFactory {

    var playerHandFactory: PlayerHandFactory = RandomPlayerHandFactory(random)
    private var nextId = 0L

    override fun create(): Player {
        val id = nextId++
        return Player(
            id = id,
            name = "PlayerName-$id",
            isAlive = true,
            hand = playerHandFactory.create()
        )
    }
}