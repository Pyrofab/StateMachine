open class ActionState<T>(name: String, finalState: Boolean = false,
    protected val op: ((Machine) -> Unit)? = null) : State<T>(name, finalState) {
  open fun action(machine: Machine) = op?.invoke(machine)
}

class ActionStateNoInput<T>(name: String, finalState: Boolean = false,
    op: ((Machine) -> Unit)? = null,
    protected val op1: ((State<T>, Machine) -> Unit)? = null) : ActionState<T>(name, finalState, op) {
  var nextState: State<T>? = null
  var errored = false
  override fun action(machine: Machine) = op1?.invoke(this, machine) ?: op?.invoke(machine)
  override fun nextState(key: T) = nextState()
  fun nextState():State<T>? = if(errored) null else nextState
}
