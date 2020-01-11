package org.tumba.explodingkittens.core

interface IntermediateStateMatcher {

    fun matches(state: IntermediateGameState): Boolean
}

class EqualTo(private val state: IntermediateGameState) :
    IntermediateStateMatcher {

    override fun matches(state: IntermediateGameState): Boolean = this.state == state
}

class Is<T : IntermediateGameState>(private val state: Class<T>) :
    IntermediateStateMatcher {

    private var andMatcher: ((T) -> Boolean)? = null

    @Suppress("UNCHECKED_CAST")
    override fun matches(state: IntermediateGameState): Boolean {
        return this.state == state::class.java && andMatcher?.invoke(state as T) ?: true
    }

    infix fun and(matcher: (T) -> Boolean) : IntermediateStateMatcher {
        andMatcher = matcher
        return this
    }
}

class And(
    private val matcher1: IntermediateStateMatcher,
    private val matcher2: IntermediateStateMatcher
) : IntermediateStateMatcher {

    override fun matches(state: IntermediateGameState): Boolean {
        return matcher1.matches(state) && matcher2.matches(state)
    }

}

infix fun IntermediateStateMatcher.and(other: IntermediateStateMatcher): IntermediateStateMatcher {
    return And(this, other)
}