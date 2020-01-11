package org.tumba.explodingkittens.utils

import org.tumba.explodingkittens.core.Card
import org.tumba.explodingkittens.core.CardType
import kotlin.random.Random

class CardFactory(private val random: Random) {

    fun create(): Card {
        return Card(
            id = random.nextLong(),
            type = CardType.values().random(random),
            kind = random.nextInt().toString()
        )
    }

    fun createOfType(cardType: CardType): Card {
        return Card(
            id = random.nextLong(),
            type = cardType,
            kind = random.nextInt().toString()
        )
    }

    fun createOfTypes(cardTypes: List<CardType>): Card {
        return Card(
            id = random.nextLong(),
            type = cardTypes.random(),
            kind = random.nextInt().toString()
        )
    }
}