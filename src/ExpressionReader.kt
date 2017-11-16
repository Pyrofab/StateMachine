/**
 * Created by E164487Q on 15/11/17.
 */
object StateFactory {
  operator fun invoke(vararg matrix: List<Char>): List<State<Char>> {
    val states = mutableListOf<State<Char>>()
    for (i in 0 until matrix.size) {
	states += State<Char>("E$i", i == matrix.size -1)
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

class State<T>(val name: String, val finalState: Boolean = false) {
  var transitions: Map<T, State<T>> = mapOf()
  fun nextState(key: T) = transitions[key]
}

open class Analyser<T>(val initialState: State<T>) {

  fun analyse(chain: Array<T>): Boolean {
    var curState: State<T> = initialState
    var i = 0
    while(!curState.finalState && ++i < chain.size) {
      println("${curState.name} + ${chain[i]} => ${curState.nextState(chain[i])?.name ?: "null"}")
      curState = curState.nextState(chain[i]) ?: break
    }
    return curState.finalState
  }
}

class StringAnalyser(initialState: State<Char>) : Analyser<Char>(initialState) {
  fun analyse(chain: String): Boolean {
    var curState: State<Char> = initialState
    var i = 0
    while(!curState.finalState && i < chain.length) {
      println("${curState.name} + ${chain[i]} => ${curState.nextState(chain[i])?.name ?: "null"}")
      curState = curState.nextState(chain[i]) ?: break
      i++
    }
    return curState.finalState
  }
}
