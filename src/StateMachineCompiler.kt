object StateMachineCompiler {
    val compiler: StateMachine<Char>

    init {
        var allStates = mutableListOf(
                State<Char>("E0-declarations"),
                State("E1-declarations-end_line"),
                State("E2-def-name"),
                ActionState("E3-def-name", op = { it.add() }),
                ActionState("E4-def-body", op = { it.pop(); it.pop("state_name") }),
                ActionState("E5-def-body"),
                ActionState("E6-def-body"),
                ActionState("E7-def-body-final", op = { it.pop(); it.pop("is_final"); }),
                ActionState("E8-def-body-final-end"),
                ActionState("E9-def-body-op"),
                ActionState("E10-def-body-op", op = { it.add() }),
                ActionState("E11-def-body-op-end", op = { it.pop("operation") }),
                ActionState("E12-def-body-no_input", op = { it.pop("no_input") }),
                ActionState("E13-def-body-no_input-end"),
                ActionState("E14-def-body-end", op = {
                    it.heap["${it.heap["state_name"]}"] =
                            if (it.heap["no_input"] == '1')
                                ActionStateNoInput("${it.heap["state_name"]}", it.heap["is_final"] == '1')
                            else
                                ActionState<Char>("${it.heap["state_name"]}", it.heap["is_final"] == '1')
                }),
                ActionState("E15-def-body-end_line"),
                ActionState("E16-declarations-end"),
                ActionState("E17-declarations-end"),
                ActionState("E18-transitions"),
                ActionState("E19-transitions-name1"),
                ActionState("E20-transitions-name1", op = { it.add() }),
                ActionState("E21-transitions-parameter", op = { it.pop("e0") }),
                ActionState("E22-transitions-parameter", op = { it.pop("char") }),
                ActionState("E23-transitions-parameter"),
                ActionState("E24-transitions-arrow1"),
                ActionState("E25-transitions-arrow2"),
                ActionState("E26-transitions-name2"),
                ActionState("E27-transitions-name2", op = { it.add() }),
                ActionState("E28-transitions-end", true, op = {
                    (it.heap["${it.heap["e0"]}"] as State<Char>).transitions +=
                            (it.heap["char"] as Char to it.heap["${it.stack.pop()}"] as State<Char>)
                }))
        allStates[0].transitions = mutableMapOf('[' to allStates[1])
        allStates[1].transitions = mutableMapOf('\n' to allStates[2])
        for (c in 'a'..'z') allStates[2].transitions += (c to allStates[3])
        for (c in 'a'..'z') allStates[3].transitions += (c to allStates[3])
        allStates[3].transitions += ('{' to allStates[4])
        allStates[4].transitions += ('\n' to allStates[5])
        allStates[5].transitions = mutableMapOf('0' to allStates[7], '1' to allStates[7])
        for (c in 'a'..'z') allStates[5].transitions += (c to allStates[9])
        allStates[7].transitions = mutableMapOf('\n' to allStates[8])
        for (c in 'a'..'z') allStates[8].transitions += (c to allStates[9])
        for (c in 'a'..'z') allStates[9].transitions += (c to allStates[9])

        compiler = StateMachine(allStates)
    }
}
