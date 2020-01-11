package org.tumba.explodingkittens.core

import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.tumba.explodingkittens.core.utils.CardFactory
import kotlin.random.Random

class CardDropImplTest {

    private lateinit var random: Random
    private lateinit var cardFactory: CardFactory
    private lateinit var cards: List<Card>
    private lateinit var drop: CardDrop

    @Before
    fun setup() {
        random = Random(0)
        cardFactory = CardFactory(random)
        cards = listOf(cardFactory.create(), cardFactory.create())
        drop = CardDropImpl(cards)
    }

    @Test
    fun `getAll should return all elements`() {
        drop.getAll() `should equal` cards
    }

    @Test
    fun `add should return add element to end`() {
        val card = cardFactory.create()
        drop.add(card)
        drop.getAll().last() `should equal` card
    }

    @Test
    fun `remove should remove at index`() {
        drop.remove(1)
        drop.getAll() `should equal` listOf(cards[0])
    }
}