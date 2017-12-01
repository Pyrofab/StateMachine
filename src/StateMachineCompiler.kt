object StateMachineCompiler {
  val compiler : StateMachine<Char>

  init {
    var allStates = mutableListOf(
      State<Char>("E0-declarations"),
      State<Char>("E1-declarations-end_line"),
      State<Char>("E2-def-name"),
      ActionState<Char>("E3-def-name", op={it.add()}),
      ActionState<Char>("E4-def-body", op={it.pop(); it.pop("state_name")}),
      ActionState<Char>("E5-def-body"),
      ActionState<Char>("E6-def-body"),
      ActionState<Char>("E7-def-body-final", op={it.pop(), it.pop("is_final")}),
      ActionState<Char>("E8-def-body-final-end"),
      ActionState<Char>("E9-def-body-op");
      ActionState<Char>("E10-def-body-op", op={it.add()}),
      ActionState<Char>("E11-def-body-op-end", op={it.pop("operation")}),
      ActionState<Char>("E12-def-body-no_input", op={it.pop("no_input")}),
      ActionState<Char>("E13-def-body-no_input-end"),
      ActionState<Char>("E14-def-body-end", op={it.heap["${it.heap["state_name"]}"] = if(it.heap["no_input"] == '1') ActionStateNoInput(it.heap["is_final"] == '1') else ActionState(it.heap["is_final"] == '1')}),
      ActionState<Char>("E15-def-body-end_line"),
      ActionState<Char>("E16-declarations-end"),
      ActionState<Char>("E17-declarations-end"),
      ActionState<Char>("E18-transitions"),
      ActionState<Char>("E19-transitions-name1"),
      ActionState<Char>("E20-transitions-name1", op={it.add()}),
      ActionState<Char>("E21-transitions-parameter", op={it.pop("e0")}),
      ActionState<Char>("E22-transitions-parameter", op={it.pop("char")}),
      ActionState<Char>("E23-transitions-parameter"),
      ActionState<Char>("E24-transitions-arrow1"),
      ActionState<Char>("E25-transitions-arrow2"),
      ActionState<Char>("E26-transitions-name2"),
      ActionState<Char>("E27-transitions-name2", op={it.add()}),
      ActionState<Char>("E28-transitions-end", true, op={(it.heap["${it.heap["e0"]}"] as State).transitions += (it.heap("char") as Char to it.heap["${it.stack.pop()}"] as State<Char>)}))
    allStates[0].transitions = mutableMapOf('[' to allStates[1])
    allStates[1].transitions = mutableMapOf('\n' to allStates[2])
    for(c in 'a'..'z') allStates[2].transitions += (c to allStates[3])
    for(c in 'a'..'z') allStates[3].transitions += (c to allStates[3])
    allStates[3].transitions += ('{' to allStates[4])
    allStates[4].transitions += ('\n' to allStates[5])
    allStates[5].transitions = mutableMapOf('0' to allStates[7], '1' to allStates[7])
    for(c in 'a'..'z') allStates[5].transitions += (c to allStates[9])
    allStates[7].transitions = mutableMapOf('\n' to allStates[8])
    allStates[8].transitions = mutableMapOf(' ' to allStates[9])
    compiler = StateMachine(allStates)
  }
}
