object StateMachineCompiler {
  val compiler : StateMachine<Char>

  init {
    var allStates = mutableListOf(
      State<Char>("E0-declarations"),
      State<Char>("E1-def-name"),
      State<Char>("E2-def-name"),
      ActionState<Char>("E3-def-name", op={it.add()}),
      ActionState<Char>("E4-def-body", op={it.pop("state_name")}),
      ActionState<Char>("E5-def-body"),
      ActionState<Char>("E6-def-body", op={it.pop("is_final")}),
      ActionState<Char>("E6-def-body-op", op={it.pop("operation")}))
    compiler = StateMachine(allStates)
  }
}
