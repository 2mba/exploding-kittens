package org.tumba.explodingkittens.utils

import org.tumba.explodingkittens.core.Card
import org.tumba.explodingkittens.core.CardStack
import org.tumba.explodingkittens.core.CardStackImpl
import org.tumba.explodingkittens.core.CardType
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

interface CardStackFactory {

    fun create(playersCount: Int): CardStack
}

class SimpleCardStackFactory(
    private val cards: List<Card>,
    private val random: Random
) : CardStackFactory {

    override fun create(playersCount: Int): CardStack {
        return CardStackImpl(cards = cards, random = random)
    }
}

class CardStackFactoryImpl(private val random: Random) : CardStackFactory {

    private val cardFactory: CardFactory = CardFactory(random)

    override fun create(playersCount: Int): CardStack {
        val totalExplodingKittensCards = 4
        val totalDefuseCards = 6

        val explodingKittensCardsCount = min(playersCount - 1, totalExplodingKittensCards)
        val explodingKittensCards = generateSequence { cardFactory.createOfType(CardType.EXPLODE) }
            .take(explodingKittensCardsCount)
            .toList()

        val defuseCardsCount = max(totalDefuseCards - playersCount, 0)
        val defuseCards = generateSequence { cardFactory.createOfType(CardType.DEFUSE) }
            .take(defuseCardsCount)
            .toList()

        val otherCardsCount = 7
        val otherCards =
            generateSequence { cardFactory.createOfTypes(CardType.values().filter { it == CardType.DEFUSE || it == CardType.EXPLODE }) }
                .take(otherCardsCount)
                .toList()

        return CardStackImpl(
            listOf(defuseCards, explodingKittensCards, otherCards).flatten(),
            random
        )
    }
}