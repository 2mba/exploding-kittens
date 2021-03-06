package org.tumba.explodingkittens.core

interface Game {

    var eventListener: (String) -> Unit

    val state: GameState

    fun init()

    fun initFrom(state: GameState)

    fun executeCommand(command: GameCommand)
}

data class GameState(
    val players: MutableList<Player>,
    val stack: CardStack,
    val drop: CardDrop,
    var intermediateGameState: IntermediateGameState
) {

    override fun toString(): String {
        return "GameState(\n\tplayers=$players,\n\tstack=$stack,\n\tdrop=$drop,\n\tintermediateGameState=$intermediateGameState\n)"
    }
}

sealed class IntermediateGameState(
    val playerId: Long,
    val numberOfCardToTakeFromStack: Int
) {

    @Suppress("EqualsOrHashCode")
    class PlayCard(
        playerId: Long,
        numberOfCardToTake: Int
    ) : IntermediateGameState(playerId, numberOfCardToTake) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false
            return true
        }
    }

    class InsertExplodeCardToStack(
        playerId: Long,
        numberOfCardToTake: Int,
        val explodeCard: Card
    ) : IntermediateGameState(playerId, numberOfCardToTake)

    class TakeCard(
        playerId: Long,
        numberOfCardToTakeFromStack: Int
    ) : IntermediateGameState(playerId, numberOfCardToTakeFromStack)

    class ReturnExplodingCard(
        playerId: Long,
        numberOfCardToTakeFromStack: Int
    ) : IntermediateGameState(playerId, numberOfCardToTakeFromStack)

    class TakeCardFromPlayer(
        playerId: Long,
        numberOfCardToTakeFromStack: Int,
        val fromPlayerId: Long
    ) : IntermediateGameState(playerId, numberOfCardToTakeFromStack) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as TakeCardFromPlayer

            if (fromPlayerId != other.fromPlayerId) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + fromPlayerId.hashCode()
            return result
        }
    }

    class GiveCardToPlayer(
        playerId: Long,
        numberOfCardToTakeFromStack: Int,
        val playerIdThatShouldGiveCard: Long
    ) : IntermediateGameState(playerId, numberOfCardToTakeFromStack)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IntermediateGameState

        if (playerId != other.playerId) return false
        if (numberOfCardToTakeFromStack != other.numberOfCardToTakeFromStack) return false

        return true
    }

    override fun hashCode(): Int {
        var result = playerId.hashCode()
        result = 31 * result + numberOfCardToTakeFromStack
        return result
    }

    class Win(
        playerId: Long
    ) : IntermediateGameState(
        playerId = playerId,
        numberOfCardToTakeFromStack = 0
    ) {
        override fun toString(): String {
            return "Win(playerId=$playerId)"
        }
    }

    override fun toString(): String {
        return "IntermediateGameState(playerId=$playerId, numberOfCardToTakeFromStack=$numberOfCardToTakeFromStack)"
    }
}

class GameManager(
    private val state: GameState,
    private val eventListener: (String) -> Unit = { }
) {

    fun event(event: String) {
        eventListener.invoke(event)
    }

    fun ensureStateThat(matcher: (IntermediateGameState) -> Boolean) {
        ensureStateThat(
            object : IntermediateStateMatcher {
                override fun matches(state: IntermediateGameState): Boolean = matcher(state)
            }
        )
    }

    fun ensureStateThat(matcher: IntermediateStateMatcher) {
        if (!matcher.matches(state.intermediateGameState)) {
            throw IllegalStateException("Incorrect state for command, state = ${this.state}")
        }
    }

    fun setIntermediateState(state: IntermediateGameState) {
        this.state.intermediateGameState = state
        eventListener.invoke("State changed to $state")
    }

    fun getPlayerById(id: Long): Player {
        return state.players.firstOrNull { it.id == id }
            ?: throw IllegalStateException("No player with id = $id, players = ${state.players}")
    }

    fun getPlayerCard(player: Player, cardId: Long): Card {
        return player.hand.getAll().firstOrNull { it.id == cardId }
            ?: throw IllegalStateException("No card for player with cardId = $cardId, player cards = ${player.hand.getAll()}")
    }

    fun currentPlayer(): Player {
        return getPlayerById(state.intermediateGameState.playerId)
    }

    fun nextPlayer(): Player {
        val players = state.players
        return players
            .indexOfFirst { it.id == state.intermediateGameState.playerId }
            .let { idx ->
                (0..(players.size - 2))
                    .asSequence()
                    .map { (it + idx + 1) % players.size }
                    .map { players[it] }
                    .firstOrNull { it.isAlive }
                    ?: throw IllegalStateException("No next player")
            }
    }

    fun getCardOfPlayer(player: Player, type: CardType): Card? {
        return player.hand
            .getAll()
            .firstOrNull { card -> card.type == type }
    }

    fun removeCardOfPlayer(player: Player, card: Card) {
        val hand = player.hand
        hand.remove(hand.getAll().indexOf(card))
    }
}

internal class GameImpl(
    state: GameState,
    private val commandProcessor: GameCommandProcessor
) : Game {

    override var eventListener: (String) -> Unit = { }

    private var _state: GameState = state

    override val state: GameState
        get() = _state

    override fun init() {
    }

    override fun initFrom(state: GameState) {
    }

    override fun executeCommand(command: GameCommand) {
        commandProcessor.process(
            command, _state,
            GameManager(_state, eventListener)
        )
    }
}