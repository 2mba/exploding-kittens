package org.tumba.explodingkittens.utils

import org.tumba.explodingkittens.core.Card
import org.tumba.explodingkittens.core.CardType
import kotlin.random.Random

class CardFactory {

    private var random = Random(0)

    fun create(): Card {
        return Card(
            id = random.nextLong(),
            type = CardType.values().random(random),
            kind = random.nextInt().toString()
        )
    }
}