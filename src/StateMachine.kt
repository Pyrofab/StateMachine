open class StateMachine<T>(protected val allStates: List<State<T>>,
      val initialState:State<T>? = if(allStates.isEmpty()) null else allStates[0]) {

  private var machine = Machine()
  private var curState : State<T>? = initialState

  fun hasFinished() = curState == null
  fun hasErrored() = !(curState?.finalState ?: true)

  fun accept(next: T) {
    do {
      curState = curState?.nextState(next) ?: break
      machine.stack.push(next as Any)
      if(curState is ActionState<T>)
        (curState as ActionState<T>).action(machine)
    } while (curState is ActionStateNoInput)

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
