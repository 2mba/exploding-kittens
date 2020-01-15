package org.tumba.explodingkittens.core

import kotlin.random.Random

data class Card(
    val id: Long,
    val type: CardType,
    val kind: String
)

enum class CardType {
    NONE,
    NOPE,
    EXPLODE,
    DEFUSE,
    LOOK_AT_THE_FUTURE,
    ATTACK,
    SKIP
}

interface CardStack {

    fun pop(): Card

    fun peek(count: Int): List<Card>

    fun size(): Int

    fun shuffle()

    fun put(index: Int, card: Card)
}

internal class CardStackImpl(
    cards: List<Card> = emptyList(),
    private val random: Random
) : CardStack {

    private val cards: MutableList<Card> = cards.reversed().toMutableList()

    override fun pop(): Card {
        if (cards.isEmpty()) throw IllegalStateException("No cards in stack")
        return cards.removeAt(cards.lastIndex)
    }

    override fun peek(count: Int): List<Card> {
        return cards.takeLast(count)
    }

    override fun size(): Int = cards.size

    override fun shuffle() {
        cards.shuffle(random)
    }

    override fun put(index: Int, card: Card) {
        cards.add(index, card)
    }
}

interface CardDrop {

    fun getAll(): List<Card>

    fun add(card: Card)

    fun remove(index: Int): Card
}

internal class CardDropImpl(
    cards: List<Card> = emptyList()
) : CardDrop {

    private val cards: MutableList<Card> = cards.toMutableList()

    override fun getAll(): List<Card> = cards

    override fun add(card: Card) {
        cards.add(card)
    }

    override fun remove(index: Int): Card {
        return cards.removeAt(index)
    }

    companion object {
        fun empty(): CardDropImpl {
            return CardDropImpl(cards = emptyList())
        }
    }
}

data class Player(
    val id: Long,
    val name: String,
    val isAlive: Boolean,
    val hand: PlayerHand
)

interface PlayerHand {

    fun getAll(): List<Card>

    fun get(index: Int): Card

    fun remove(index: Int): Card

    fun add(card: Card)

    fun size(): Int
}

internal class PlayerHandImpl(
    cards: List<Card> = emptyList()
) : PlayerHand {

    private val cards: MutableList<Card> = cards.toMutableList()

    override fun getAll(): List<Card> = cards

    override fun get(index: Int): Card = cards[index]

    override fun remove(index: Int): Card {
        return cards.removeAt(index)
    }

    override fun add(card: Card) {
        cards.add(card)
    }

    override fun size(): Int = cards.size

    override fun toString(): String {
        return "PlayerHandImpl(cards=$cards)"
    }

    companion object {
        fun empty(): PlayerHandImpl {
            return PlayerHandImpl(cards = emptyList())
        }
    }
}


data class PlayerDto(
    val id: Long,
    val name: String
)
