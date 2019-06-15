package org.tumba.explodingkittens.core

import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import org.tumba.explodingkittens.utils.CardFactory

class CardStackImplTest {

    private lateinit var stack: CardStack
    private lateinit var cardFactory: CardFactory

    @Before
    fun setup() {
        stack = CardStackImpl()
        cardFactory = CardFactory()
    }

    @Test(expected = IllegalStateException::class)
    fun `pop should throw when empty`() {
        stack.pop()
    }

    @Test
    fun `pop should return last element`() {
        val cards = listOf(cardFactory.create(), cardFactory.create())
        stack = CardStackImpl(cards = cards)
        stack.pop() `should equal` cards[1]
        stack.pop() `should equal` cards[0]
    }
}