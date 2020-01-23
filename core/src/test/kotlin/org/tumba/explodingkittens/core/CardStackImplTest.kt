package org.tumba.explodingkittens.core

import org.amshove.kluent.*
import org.junit.Before
import org.junit.Test
import org.tumba.explodingkittens.core.utils.CardFactory
import kotlin.random.Random

class CardStackImplTest {

    private lateinit var random: Random
    private lateinit var cardFactory: CardFactory
    private lateinit var cards: List<Card>
    private lateinit var stack: CardStack

    @Before
    fun setup() {
        random = Random(0)
        cardFactory = CardFactory(random)
        cards = listOf(cardFactory.create(), cardFactory.create())
        stack = CardStackImpl(cards, random)
    }

    @Test(expected = IllegalStateException::class)
    fun `pop should throw when empty`() {
        stack = CardStackImpl(random = random)
        stack.pop()
    }

    @Test
    fun `pop should return first element`() {
        stack.pop() `should equal` cards[0]
        stack.size() `should equal` 1
        stack.pop() `should equal` cards[1]
        stack.size() `should equal` 0
    }

    @Test
    fun `peek should return empty list when empty`() {
        stack = CardStackImpl(random = random)
        stack.peek(1).`should be empty`()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `peek should throw when count lt 0`() {
        stack.peek(-1)
    }

    @Test
    fun `peek should return first elements`() {
        stack.peek(1) `should equal` listOf(cards[0])
        stack.peek(2) `should equal` listOf(cards[0], cards[1])
        stack.peek(3) `should equal` listOf(cards[0], cards[1])
    }

    @Test
    fun `size should return 0 when empty`() {
        stack = CardStackImpl(random = random)
        stack.size() `should equal` 0
    }

    @Test
    fun `size should return size`() {
        stack.size() `should equal` cards.size
    }

    @Test
    fun `shuffle should change cards order`() {
        val cards = generateSequence { cardFactory.create() }
            .take(5)
            .toList()
        stack = CardStackImpl(cards = cards, random = random)
        stack.shuffle()

        stack.peek(cards.size) `should contain all` cards
        stack.peek(cards.size) `should not be` cards
        stack.size() `should equal` cards.size
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `put should throw when index gte size`() {
        val card = cardFactory.create()
        stack.put(cards.size + 1, card)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `put should throw when index lt zero`() {
        val card = cardFactory.create()

        stack.put(-1, card)
    }

    @Test
    fun `put should add card to stack`() {
        cards = listOf(
            cardFactory.create(),
            cardFactory.create(),
            cardFactory.create())

        stack = CardStackImpl(random = random)

        stack.put(0, cards[0])
        stack.put(1, cards[1])
        stack.put(1, cards[2])

        stack.size() `should be equal to` cards.size
        stack.peek(3) `should equal` listOf(cards[0], cards[2], cards[1])
    }
}