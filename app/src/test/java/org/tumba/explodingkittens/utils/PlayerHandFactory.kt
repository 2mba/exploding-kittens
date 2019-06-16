package org.tumba.explodingkittens.utils

import org.tumba.explodingkittens.core.PlayerHand
import org.tumba.explodingkittens.core.PlayerHandImpl
import kotlin.random.Random


class PlayerHandFactory(random: Random) {
    private val cardFactory: CardFactory = CardFactory(random)

    fun create(size: Int = 4): PlayerHand {
        val cards = generateSequence { cardFactory.create() }
            .take(size)
            .toList()

        return PlayerHandImpl(cards)
    }
}