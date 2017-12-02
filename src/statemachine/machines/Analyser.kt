package statemachine.machines

import statemachine.states.State

/**
 * Classe permettant d'analyser une chaîne d'éléments quelconque à l'aide d'un automate
 *
 * [stateMachine] l'automate utilisé pour la reconnaissance
 */
open class Analyser<in T>(val stateMachine: StateMachine<T>) {

    fun analyse(chain: Iterable<T>): Boolean = analyse(chain.iterator())

    /**
     * Analyse une séquence d'éléments
     *
     * @return vrai si l'automate est dans un état final à la fin de la séquence
     */
    fun analyse(iterator: Iterator<T>): Boolean {
        this.stateMachine.reset()
        while (iterator.hasNext() && stateMachine.accept(iterator.next())) {}
        return !stateMachine.hasErred()
    }

    override fun toString(): String = "Analyser(stateMachine=$stateMachine)"

}

/**
 * Classe spécialisée dans la reconnaissance de chaînes de caractères
 */
class StringAnalyser(stateMachine: StateMachine<Char>) : Analyser<Char>(stateMachine) {
    constructor(allStates: List<State<Char>>) : this(StateMachine(allStates))

    fun analyse(chain: String): Boolean = super.analyse(chain.asIterable())
}