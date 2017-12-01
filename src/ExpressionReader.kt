/**
 * Created by Fabien on 15/11/17.
 */
open class State<T>(val name: String, val finalState: Boolean = false) {
    companion object StateFactory {
        fun create(vararg matrix: List<Char>): List<State<Char>> {
            val states = mutableListOf<State<Char>>()
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
}
