package org.tumba.explodingkittens.utils

import org.tumba.explodingkittens.core.CardStack
import org.tumba.explodingkittens.core.CardStackImpl
import org.tumba.explodingkittens.core.CardType
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random


class CardStackFactory(private val random: Random) {

    private val cardFactory: CardFactory = CardFactory(random)

    fun create(playersCount: Int): CardStack {
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
        val otherCards =  generateSequence { cardFactory.createOfTypes(CardType.values().filter { it == CardType.DEFUSE || it == CardType.EXPLODE }) }
            .take(otherCardsCount)
            .toList()

        return CardStackImpl(
            listOf(defuseCards, explodingKittensCards, otherCards).flatten(),
            random)
    }
}