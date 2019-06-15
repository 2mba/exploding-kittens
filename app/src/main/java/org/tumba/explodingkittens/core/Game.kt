package org.tumba.explodingkittens.core

interface Game {

    val state: GameState

    fun init()

    fun initFrom(state: GameState)

    fun executeCommand(command: GameCommand)
}

data class GameState(
    val players: List<Player>,
    val stack: CardStack,
    val drop: CardDrop,
    var intermediateGameState: IntermediateGameState
)

sealed class IntermediateGameState {

    data class PlayCard(val playerId: Long) : IntermediateGameState()

    data class TakeCard(val playerId: Long) : IntermediateGameState()

    data class ReturnExplodingCard(val playerId: Long) : IntermediateGameState()

    data class TakeCardFromPlayer(val playerId: Long, val fromPlayerId: Long) : IntermediateGameState()

    data class GiveCardToPlayer(val playerId: Long, val playerIdThatShouldGiveCard: Long) : IntermediateGameState()
}

class GameManager(private val state: GameState) {

    fun ensureStateThat(matcher: IntermediateStateMatcher) {
        if (!matcher.matches(state.intermediateGameState)) {
            throw IllegalStateException("Incorrect state for command, state = ${this.state}")
        }
    }
}

interface IntermediateStateMatcher {

    fun matches(state: IntermediateGameState): Boolean
}

class EqualTo(private val state: IntermediateGameState): IntermediateStateMatcher {

    override fun matches(state: IntermediateGameState): Boolean = this.state == state
}

class GameImpl(
    state: GameState,
    private val commandProcessor: GameCommandProcessor
) : Game {

    private var _state: GameState = state

    override val state: GameState
        get() = _state

    override fun init() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initFrom(state: GameState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun executeCommand(command: GameCommand) {
        commandProcessor.process(command, _state, GameManager(_state))
    }
}
