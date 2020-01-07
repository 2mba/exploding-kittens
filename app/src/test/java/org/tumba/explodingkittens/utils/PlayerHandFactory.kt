package org.tumba.explodingkittens.utils

import org.tumba.explodingkittens.core.Card
import org.tumba.explodingkittens.core.PlayerHand
import org.tumba.explodingkittens.core.PlayerHandImpl
import kotlin.random.Random

interface PlayerHandFactory {

    fun create(playerId: Long): PlayerHand

    companion object {

        fun create(playerHandCreator: () -> PlayerHand): PlayerHandFactory {
            return object : PlayerHandFactory {

                override fun create(playerId: Long): PlayerHand = playerHandCreator.invoke()
            }
        }
    }
}

class RandomPlayerHandFactory(
    random: Random,
    private val size: Int = 4
) : PlayerHandFactory {
    private val cardFactory: CardFactory = CardFactory(random)

    override fun create(playerId: Long): PlayerHand {
        val cards = generateSequence { cardFactory.create() }
            .take(size)
            .toList()

        return PlayerHandImpl(cards)
    }
}

class SimplePlayerHandFactory(
    private val cards: List<Card>
) : PlayerHandFactory {

    override fun create(playerId: Long): PlayerHand {
        return PlayerHandImpl(cards)
    }
}