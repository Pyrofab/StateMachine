package statemachine.machines

import statemachine.states.ActionState
import statemachine.states.ActionStateNoInput
import statemachine.states.State
import java.io.File
import java.util.*
import kotlin.reflect.full.memberProperties

/**
 * Objet permettant de compiler un fichier de description d'automate en un automate pourvu d'actions.
 *
 * Actuellement ne peut que reconnaître que le fichier est valide.
 * WARNING : this is very very very bad code. Don't look at it if you value your sanity
 */
object StateMachineCompiler {
    val compiler: StateMachine<String>

    object O {
        val e0 = State<String>("E0-declarations")
        val e1 = State<String>("E1-declarations-end_line")
        val e2 = State<String>("E2-def-name")
        val e3 = State<String>("E3-def-name")
        val e4 = ActionState<String>("E4-def-name") { it.add() }
        val e4_1 = ActionState<String>("E4.1") { it.pop() }
        val e4_2 = ActionStateNoInput<String>("E4.2") {
            it.pop("\$temp")
            it.pop("\$state_name")
            it.heap["${it.heap["\$state_name"]}"] = ActionState<Char>("${it.heap["\$state_name"]}")
            it.push("\$temp")
        }
        val e4_3 = ActionStateNoInput<String>("E4.2") {
            it.pop("\$temp")
            it.pop("\$state_name")
            it.heap["${it.heap["\$state_name"]}"] = ActionState<Char>("${it.heap["\$state_name"]}")
            it.push("\$temp")
        }
        val e5 = ActionState<String>("E5-def-body") { it.pop(); it.pop("\$state_name") }
        val e6 = ActionState<String>("E6-def-body")
        val e8 = ActionState<String>("E8-def-body-final") { it.pop("\$is_final"); }
        val e9 = ActionState<String>("E9-def-body-final-end")
        val e10 = ActionState<String>("E10-def-body-op") { it.add() }
        val e11 = ActionState<String>("E11-def-body-op") { it.pop(); it.pop("\$operation") }
        val e12 = ActionState<String>("E12-def-body-op-end_line")
        val e12_1 = State<String>("E12.1")
        val e13 = ActionState<String>("E13-def-body-no_input")
        val e13_1 = State<String>("E13.1")
        val e14 = ActionState<String>("E14-def-body-no_input-end_line")
        val e14_1 = State<String>("E14.1")
        val e15 = ActionState<String>("E15-def-body-end") {
            it.heap["${it.heap["\$state_name"]}"] =
                    ActionState<Char>("${it.heap["\$state_name"]}", it.heap["\$is_final"] == "1",
                            compileOperation("${it.heap["\$operation"]}"))
        }
        val e16 = ActionState<String>("E16") {
            it.heap["${it.heap["\$state_name"]}"] =
                    ActionStateNoInput<Char>("${it.heap["\$state_name"]}", it.heap["\$is_final"] == "1",
                            compileOperation("${it.heap["\$operation"]}"))
        }
        val e17 = ActionState<String>("E17-def-body-end_line")
        val e18 = ActionState<String>("E18-declarations-end", true)
        val e19 = ActionState<String>("E19")
        val e20 = ActionState<String>("E20-declarations-end") { it.add() }
        val e21 = ActionState<String>("E21-transitions") { it.pop(); it.pop("\$e0") }
        val e22 = ActionState<String>("E22-transitions-name1") { it.pop("\$char") }
        val e22_1 = ActionState<String>("E22-1")
        val e22_2 = ActionState<String>("E22-2") { it.heap["\$chars"] = "${it.heap["\$char"]}"[0].."${it.stack.pop()}"[0] }
        val e23 = ActionState<String>("E23-transitions-name1")
        val e24 = ActionState<String>("E24-transitions-parameter")
        val e25 = ActionState<String>("E25-transitions-parameter")
        val e26 = ActionState<String>("E26-transitions-parameter")
        val e27 = ActionState<String>("E27") { it.add() }
        val e28 = ActionStateNoInput<String>("E28-transitions-end", true) {
            it.pop()
            val state = it.heap[it.stack.pop()] as State<String>
            if(it.heap["\$chars"] is CharRange) {
                for(c in (it.heap["\$chars"] as CharRange))
                    (it.heap["${it.heap["\$e0"]}"] as State<String>).transitions +=
                            ("$c" to state)
                it.heap.remove("\$chars")
            } else
                (it.heap["${it.heap["\$e0"]}"] as State<String>).transitions +=
                    (it.heap["\$char"] as String to state)
        }
    }

    init {
        O.e0.transitions = mutableMapOf("[" to O.e1)
        O.e1.transitions = mutableMapOf("\n" to O.e2, "\r" to O.e1)
        for (c in 'a'..'z') O.e2.transitions += "$c" to O.e3
        O.e3.transitions = mutableMapOf("{" to O.e5)
        for (c in 'a'..'z') O.e3.transitions += "$c" to O.e4
        for (c in '0'..'9') O.e3.transitions += "$c" to O.e4
        O.e4.transitions = mutableMapOf("{" to O.e5, "\r" to O.e4_1, "\n" to O.e4_1, " " to O.e4_1, "\t" to O.e4_1)
        for (c in 'a'..'z') O.e4.transitions += "$c" to O.e4
        for (c in '0'..'9') O.e4.transitions += "$c" to O.e4
        O.e4_1.transitions = mutableMapOf("{" to O.e5, "\r" to O.e4_1, "\n" to O.e4_1, " " to O.e4_1, "\t" to O.e4_1, "]" to O.e4_3)
        for (c in 'a'..'z') O.e4_1.transitions += "$c" to O.e4_2
        O.e4_2.nextState = O.e3
        O.e4_3.nextState = O.e17
        O.e5.transitions = mutableMapOf("\n" to O.e6, "\r" to O.e6)
        O.e6.transitions = mutableMapOf("\n" to O.e6, "0" to O.e8, "1" to O.e8)
        for (c in 'a'..'z') O.e6.transitions += "$c" to O.e10
        O.e8.transitions = mutableMapOf("\n" to O.e9, "\r" to O.e9)
        O.e9.transitions = mutableMapOf("\n" to O.e9, "0" to O.e12, "1" to O.e12, "}" to O.e14)
        for (c in 'a'..'z') O.e9.transitions += "$c" to O.e10
        O.e10.transitions = mutableMapOf(" " to O.e10, "\r" to O.e11, "\n" to O.e11)
        for (c in 'a'..'z') O.e10.transitions += "$c" to O.e10
        for (c in '0'..'9') O.e10.transitions += "$c" to O.e10
        O.e11.transitions = mutableMapOf("\n" to O.e11, "0" to O.e12, "1" to O.e12_1)
        O.e12.transitions = mutableMapOf("\n" to O.e13, "\r" to O.e13)
        O.e12_1.transitions = mutableMapOf("\n" to O.e13_1, "\r" to O.e13_1)
        O.e13.transitions = mutableMapOf("\n" to O.e13, "}" to O.e14)
        O.e13_1.transitions = mutableMapOf("\n" to O.e13_1, "}" to O.e14_1)
        O.e14.transitions = mutableMapOf("\n" to O.e15, "\r" to O.e14)
        O.e14_1.transitions = mutableMapOf("\n" to O.e16, "\r" to O.e14_1)
        O.e15.transitions = O.e2.transitions + ("]" to O.e17)
        O.e16.transitions = O.e2.transitions + ("]" to O.e17)
        O.e17.transitions = mutableMapOf("\n" to O.e18, "\r" to O.e18)
        O.e18.transitions = mutableMapOf("\n" to O.e18)
        for (c in 'a'..'z') O.e18.transitions += "$c" to O.e19
        O.e19.transitions = mutableMapOf("(" to O.e21)
        for (c in 'a'..'z') O.e19.transitions += "$c" to O.e20
        for (c in '0'..'9') O.e19.transitions += "$c" to O.e20
        O.e20.transitions = mutableMapOf("(" to O.e21)
        for (c in 'a'..'z') O.e20.transitions += "$c" to O.e20
        for (c in '0'..'9') O.e20.transitions += "$c" to O.e20
        for (c in 'a'..'z') O.e21.transitions += "$c" to O.e22
        for (c in '0'..'9') O.e21.transitions += "$c" to O.e22
        O.e22.transitions = mutableMapOf(")" to O.e23, "-" to O.e22_1)
        for (c in 'a'..'z') O.e22_1.transitions += "$c" to O.e22_2
        for (c in '0'..'9') O.e22_1.transitions += "$c" to O.e22_2
        O.e22_2.transitions = mutableMapOf(")" to O.e23)
        O.e23.transitions = mutableMapOf("=" to O.e24)
        O.e24.transitions = mutableMapOf(">" to O.e25)
        for (c in 'a'..'z') O.e25.transitions += "$c" to O.e26
        O.e26.transitions = mutableMapOf("\n" to O.e28, "\r" to O.e28, "\u001a" to O.e28)
        for (c in 'a'..'z') O.e26.transitions += "$c" to O.e27
        for (c in '0'..'9') O.e26.transitions += "$c" to O.e27
        O.e27.transitions = mutableMapOf("\n" to O.e28, "\r" to O.e28, "\u001a" to O.e28)
        for (c in 'a'..'z') O.e27.transitions += "$c" to O.e27
        for (c in '0'..'9') O.e27.transitions += "$c" to O.e27
        O.e28.nextState = O.e18

        compiler = StateMachine(O::class.memberProperties.map { it.get(O) }.filter { it is State<*> } as List<State<String>>)
    }

    fun compileStateMachine(fileName: String) {
        val analyzer = Analyser(compiler)
        println(analyzer.analyse(Scanner(File(fileName)).useDelimiter("")))
        val e0 = analyzer.stateMachine.machine.heap["e0"]
        if(e0 is State<*>)
            println(StateMachine.from(e0).varDump())
    }

    @JvmStatic
    fun main(args: Array<String>) {
        compileStateMachine("data/custom.txt")
    }
}