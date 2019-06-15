package org.tumba.explodingkittens.core

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
    ATTACK
}

interface CardStack {

    fun pop(): Card

    fun peek(count: Int): List<Card>

    fun size(): Int

    fun shuffle()

    fun put(index: Int, card: Card)
}

class CardStackImpl(cards: List<Card>) : CardStack {

    private val cards: MutableList<Card> = cards.toMutableList()

    constructor(): this(emptyList())

    override fun pop(): Card {
        if (cards.isEmpty()) throw IllegalStateException("No cards in stack")
        return cards.removeAt(cards.lastIndex)
    }

    override fun peek(count: Int): List<Card> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun size(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun shuffle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun put(index: Int, card: Card) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

interface CardDrop {

    fun getAll(): List<Card>

    fun add(card: Card)

    fun remove(index: Int): Card
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
}

interface Game {

    val state: GameState

    fun init()

    fun initFrom(state: GameState)

    fun playCard(playerId: Long, cards: List<Card>)

    fun skipPlayCard(playerId: Long)

    fun takeCard(playerId: Long)

    fun returnExplodingCard(playerId: Long, index: Int)
}

data class GameState(
    val players: List<Player>,
    val stack: CardStack,
    val drop: CardDrop,
    var intermediateGameState: IntermediateGameState
)

sealed class IntermediateGameState {

    class PlayCard(playerId: Long) : IntermediateGameState()

    class TakeCard(playerId: Long) : IntermediateGameState()

    class ReturnExplodingCard(playerId: Long) : IntermediateGameState()

    class TakeCardFromPlayer(playerId: Long, fromPlayerId: Long) : IntermediateGameState()

    class GiveCardToPlayer(playerId: Long, playerIdThatShouldGiveCard: Long) : IntermediateGameState()
}


data class PlayerDto(
    val id: Long,
    val name: String
)
