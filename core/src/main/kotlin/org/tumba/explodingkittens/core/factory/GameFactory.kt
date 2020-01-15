package org.tumba.explodingkittens.core.factory

import org.tumba.explodingkittens.core.*
import org.tumba.explodingkittens.core.processor.AttackCardProcessor
import org.tumba.explodingkittens.core.processor.PlayCardCommandProcessor
import org.tumba.explodingkittens.core.processor.SkipCardProcessor
import org.tumba.explodingkittens.core.processor.StopPlayCardCommandProcessor
import kotlin.random.Random

interface GameFactory {

    fun createGame(): Game

    companion object {
        fun create(gameStateFactory: GameStateFactory): GameFactory {
            return GameFactoryImpl(gameStateFactory, GameCommandProcessorFactory().create())
        }
    }
}

class GameFactoryImpl(
    private val gameStateFactory: GameStateFactory,
    private val commandProcessor: GameCommandProcessor
) : GameFactory {

    override fun createGame(): Game {
        return GameImpl(gameStateFactory.createGameState(), commandProcessor)
    }
}

interface GameStateFactory {

    fun createGameState(): GameState
}

class PlayerHandFactory {

    fun createPlayerHand(): PlayerHand {
        return PlayerHandImpl.empty()
    }
}

class InitialGameStateFactory(
    private val players: List<Player>,
    private val random: Random
) : GameStateFactory {

    private val cardStackFactory = InitialCardFactory()

    override fun createGameState(

    ): GameState {
        val players = players.toMutableList()
        return GameState(
            players = players,
            stack = cardStackFactory.createCardForStackAndPlayers(players, random),
            drop = CardDropImpl.empty(),
            intermediateGameState = createInitialIntermediateGameState(players, random)
        )
    }

    private fun createInitialIntermediateGameState(
        players: List<Player>,
        random: Random
    ): IntermediateGameState.PlayCard {
        return IntermediateGameState.PlayCard(
            playerId = players.random(random).id,
            numberOfCardToTake = 1
        )
    }
}


private class InitialCardFactory {

    private var cardId = 0L

    fun createCardForStackAndPlayers(players: List<Player>, random: Random): CardStack {
        val cards = createCardsWithoutExplosionCards(players.size)
        cards.shuffle(random)
        dealCardsToPlayers(cards, players)
        dealDefuseCardsToPlayers(players)
        addToStackExplosionCards(players.size, cards)
        cards.shuffle(random)
        return CardStackImpl(
            random = random,
            cards = cards
        )
    }

    private fun createCardsWithoutExplosionCards(numberOfPlayers: Int): MutableList<Card> {
        return mutableListOf<Card>().apply {
            repeat(30) {
                val id = cardId++
                add(Card(id = id, type = CardType.NONE, kind = "Card $id"))
            }
            repeat(5) {
                val id = cardId++
                add(Card(id = id, type = CardType.ATTACK, kind = "Card $id"))
            }
            repeat(5) {
                val id = cardId++
                add(Card(id = id, type = CardType.DEFUSE, kind = "Card $id"))
            }
        }
    }

    private fun dealCardsToPlayers(cards: MutableList<Card>, players: List<Player>) {
        players.forEach { player ->
            repeat(NUMBER_OF_PLAYER_CARDS_ON_START - 1) {
                player.hand.add(cards.removeAt(0))
            }
        }
    }

    private fun dealDefuseCardsToPlayers(players: List<Player>) {
        players.forEach { player ->
            val id = cardId++
            player.hand.add(
                Card(id = id, type = CardType.DEFUSE, kind = "Card $id")
            )
        }
    }

    private fun addToStackExplosionCards(numberOfPlayers: Int, cards: MutableList<Card>) {
        repeat(numberOfPlayers - 1) {
            val id = cardId++
            cards.add(Card(id = id, type = CardType.EXPLODE, kind = "Card $id"))
        }
    }

    companion object {
        private const val NUMBER_OF_PLAYER_CARDS_ON_START = 6
    }
}

private class GameCommandProcessorFactory {

    fun create(): GameCommandProcessor {
        return BatchGameCommandProcessor(
            processors = listOf(
                PlayCardCommandProcessor(
                    playCardProcessors = listOf(
                        SkipCardProcessor(),
                        AttackCardProcessor()
                    )
                ),
                StopPlayCardCommandProcessor()
            )
        )
    }
}