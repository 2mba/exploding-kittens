package org.tumba.explodingkittens.utils

import org.tumba.explodingkittens.core.Player
import kotlin.random.Random


class PlayerFactory(private val random: Random) {

    private val playerHandFactory: PlayerHandFactory = PlayerHandFactory(random)

    fun create(): Player {
        return Player(
            random.nextLong(),
            "PlayerName-" + random.nextInt(),
            true,
            playerHandFactory.create())
    }
}