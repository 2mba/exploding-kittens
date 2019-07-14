package org.tumba.explodingkittens.utils

import org.tumba.explodingkittens.core.PlayerHand
import org.tumba.explodingkittens.core.PlayerHandImpl
import kotlin.random.Random

interface PlayerHandFactory {

    fun create(size: Int = 4): PlayerHand

    companion object {

        fun create(playerHandCreator: () -> PlayerHand): PlayerHandFactory {
            return object : PlayerHandFactory {

                override fun create(size: Int): PlayerHand = playerHandCreator.invoke()
            }
        }
    }
}

class RandomPlayerHandFactory(random: Random) : PlayerHandFactory {
    private val cardFactory: CardFactory = CardFactory(random)

    override fun create(size: Int): PlayerHand {
        val cards = generateSequence { cardFactory.create() }
            .take(size)
            .toList()

        return PlayerHandImpl(cards)
    }
}