package org.tumba.explodingkittens.core

import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.tumba.explodingkittens.core.utils.CardFactory
import kotlin.random.Random

class PlayerHandImplTest {

    private lateinit var random: Random
    private lateinit var cardFactory: CardFactory
    private lateinit var cards: List<Card>
    private lateinit var hand: PlayerHand

    @Before
    fun setup() {
        random = Random(0)
        cardFactory = CardFactory(random)
        cards = listOf(cardFactory.create(), cardFactory.create())
        hand = PlayerHandImpl(cards)
    }

    @Test
    fun `getAll should return all elements`() {
        hand.getAll() `should equal` cards
    }

    @Test
    fun `get should return at index`() {
        hand.get(1) `should equal` cards[1]
    }

    @Test
    fun `add should return add element to end`() {
        val card = cardFactory.create()
        hand.add(card)
        hand.getAll().last() `should equal` card
    }

    @Test
    fun `remove should remove at index`() {
        hand.remove(1) `should equal` cards[1]
        hand.getAll() `should equal` listOf(cards[0])
    }
}