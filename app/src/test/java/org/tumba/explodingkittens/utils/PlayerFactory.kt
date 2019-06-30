package org.tumba.explodingkittens.utils

import org.tumba.explodingkittens.core.Player
import kotlin.random.Random

interface PlayerFactory {

    fun create(): Player
}

class RandomPlayerFactory(private val random: Random) : PlayerFactory {

    private val playerHandFactory: PlayerHandFactory = PlayerHandFactory(random)

    override fun create(): Player {
        return Player(
            random.nextLong(MAX_RANDOM_INT.toLong()),
            "PlayerName-" + random.nextInt(MAX_RANDOM_INT),
            true,
            playerHandFactory.create())
    }

    companion object {
        private const val MAX_RANDOM_INT = 100
    }
}