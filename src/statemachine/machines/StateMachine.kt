package statemachine.machines

import statemachine.states.ActionState
import statemachine.states.ActionStateNoInput
import statemachine.states.State

/**
 * Classe décrivant un automate de Moore
 *
 * [allStates] une liste d'états composant l'automate
 * [initialState] un état à utiliser comme point d'entrée. Si aucun n'est passé en paramètre,
 * correspond au premier état de la liste
 *
 * @author Fabien
 */
open class StateMachine<in T>(private val allStates: List<State<T>>,
                              private val initialState: State<in T>? = if (allStates.isEmpty()) null else allStates[0]) {

    companion object {
        fun <T> from(e0: State<T>): StateMachine<T> = StateMachine(getAllChildren(e0, mutableSetOf(e0)).toList(), e0)

        fun <T> getAllChildren(e0: State<T>, allStates: MutableSet<State<T>>): Set<State<T>> {
            val unknown = e0.transitions.values - allStates
            allStates += e0.transitions.values
            unknown.forEach { getAllChildren(it, allStates) }
            return allStates
        }
    }

    var machine = Machine()
        private set

    private var curState: State<in T>? = initialState

    fun hasErred() = !(curState?.finalState ?: true)

    /**
     * Réinitialise l'automate
     */
    fun reset() {
        curState = initialState
    }

    /**
     * Utilise la clé passée en paramètre pour faire avancer l'automate d'un état
     */
    fun accept(next: T): Boolean {
        do {
            curState = curState?.nextState(next) ?: return false
            machine.stack.push(next as Any)
            println("${when(next) {"\r" -> "\\r"; "\n" -> "\\n"; " " -> "\\s"; else -> "$next "}}-> ${curState?.name}")
            if (curState is ActionState<in T>)
                (curState as ActionState<in T>).action(machine)
        } while (curState is ActionStateNoInput)
        return true
    }

    override fun toString(): String = "State Machine (${allStates.size} states)"

    fun varDump() : String {
        var ret = "${toString()} {\n"
        allStates.forEach { state -> ret += "$state\n" }
        ret += "Current state: ${curState?.name}\n"
        return ret + "}"
    }

}
