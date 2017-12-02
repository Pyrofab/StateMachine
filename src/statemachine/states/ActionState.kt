package statemachine.states

import statemachine.machines.Machine

/**
 * Classe décrivant un état effectuant une action spécifique après une transition
 *
 * [op] Une fonction interagissant avec la machine virtuelle de l'automate
 *
 * @author Fabien
 */
open class ActionState<T>(name: String, finalState: Boolean = false,
                          protected val op: ((Machine) -> Unit)? = null) : State<T>(name, finalState) {
    fun action(machine: Machine) = op?.invoke(machine)
}

/**
 * Classe décrivant un état effectuant une action spécifique et qui ne requiert pas d'entrée utilisateur pour passer à l'état suivant
 */
class ActionStateNoInput<T>(name: String, finalState: Boolean = false,
                            op: ((Machine) -> Unit)? = null) : ActionState<T>(name, finalState, op) {

    var nextState: State<T>? = null
    var erred = false

    override fun nextState(key: T) = nextState()
    fun nextState(): State<T>? = if (erred) null else nextState
}
