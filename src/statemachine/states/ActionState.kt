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
    var nextState: State<T>? = null
    fun hasNoInput() = (this is ActionStateNoInput<*> || nextState != null)
    fun action(machine: Machine) = op?.invoke(machine)
    override fun nextState(key: T) = super.nextState(key) ?: nextState()
    fun nextState(): State<T>? = nextState

    override fun toString(): String {
        if(this.hasNoInput())
            return "$name {$name -> ${nextState?.name}}"
        var ret = "$name {\n"
        for (transition in this.transitions)
            ret += "    ${this.name} -> ${transition.value.name}[label=${transition.key}];\n"
        return "$ret$op\n}"
    }
}

/**
 * Classe décrivant un état effectuant une action spécifique et qui ne requiert pas d'entrée utilisateur pour passer à l'état suivant
 */
class ActionStateNoInput<T>(name: String, finalState: Boolean = false,
                            op: ((Machine) -> Unit)? = null) : ActionState<T>(name, finalState, op) {
    override fun nextState(key: T) = nextState()
}
