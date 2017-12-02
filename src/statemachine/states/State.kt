package statemachine.states

/**
 * Un état de l'automate
 *
 * [name] le nom de l'état
 * [finalState] si vrai, cet état sera considéré comme final
 *
 * @author Fabien
 */
open class State<T>(val name: String, val finalState: Boolean = false) {
    /**
     * L'objet usine pour les états
     */
    companion object StateFactory {
        /**
         * Génère une liste d'états correspondant à l'automate décrit dans la matrice de caractères passée en paramètre
         */
        fun <T> create(vararg matrix: List<T>): List<State<T>> {
            val states = mutableListOf<State<T>>()
            for (i in 0 until matrix.size) {
                states += State("E$i", i == matrix.size - 1)
            }
            for (i in 0 until states.size) {
                val state = states[i]
                for (j in 0 until matrix[i].size) {
                    state.transitions += matrix[i][j] to states[j]
                }
            }
            return states
        }
    }

    var transitions: Map<T, State<T>> = mapOf()
    open fun nextState(key: T) = transitions[key]

    override fun toString(): String {
        var ret = "$name {\n"
        for (transition in this.transitions)
            ret += "    ${this.name} -> ${transition.value.name}[label=${transition.key}];\n"
        return "$ret}"
    }

}
