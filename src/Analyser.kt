import java.util.*

open class Analyser<T>(protected val allStates: List<State<T>>,
    val initialState:State<T>? = if(allStates.isEmpty()) null else allStates[0]) {

  var machine = Machine()

  fun analyse(chain: Iterable<T>): Boolean {
    var curState: State<T> = initialState ?: return false
    val iterator = chain.iterator()
    while(iterator.hasNext()) {
     val next = iterator.next()
     do {
       curState = curState.nextState(next) ?: break
       machine.stack.push(next as Any)
       if(curState is ActionState<T>)
         curState.action(machine)
     } while (curState is ActionStateNoInput)
    }
    return curState.finalState
  }

  override fun toString(): String {
    var ret = "{\n"
    for(state in allStates) {
      for(transition in state.transitions.asIterable())
      ret += "${state.name} -> ${transition.value.name}[label=${transition.key}];\n"
    }
    return ret + "}"
  }
}

class StringAnalyser(allStates: List<State<Char>>) : Analyser<Char>(allStates) {
  fun analyse(chain: String): Boolean = super.analyse(chain.asIterable())
}
