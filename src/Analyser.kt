import java.util.*

open class Analyser<T>(val stateMachine: StateMachine<T>) {
  constructor(allStates: List<State<T>>) : this(StateMachine(allStates))

  fun analyse(chain: Iterable<T>): Boolean {
    val iterator = chain.iterator()
    while(iterator.hasNext() && !stateMachine.hasFinished()) {
     val next = iterator.next()
     stateMachine.accept(next)
    }
    return !stateMachine.hasErrored()
  }
}

class StringAnalyser(stateMachine: StateMachine<Char>) : Analyser<Char>(stateMachine) {
  constructor(allStates: List<State<Char>>) : this(StateMachine(allStates))
  fun analyse(chain: String): Boolean = super.analyse(chain.asIterable())
}
