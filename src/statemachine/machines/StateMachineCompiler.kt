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
        val e4_2 = ActionState<String>("E4.2") {
            it.pop("\$temp")
            it.pop("\$state_name")
            it.heap["${it.heap["\$state_name"]}"] = ActionState<Char>("${it.heap["\$state_name"]}")
            it.push("(\$temp)")
        }
        val e4_3 = ActionStateNoInput<String>("E4.3") {
            it.pop("\$temp")
            it.pop("\$state_name")
            it.heap["${it.heap["\$state_name"]}"] = ActionState<Char>("${it.heap["\$state_name"]}")
            it.push("(\$temp)")
        }
        val e4_4 = ActionState<String>("E4.4") { it.pop() }
        val e4_5 = ActionState<String>("E4.5") {
            it.pop()
            it.pop("\$state_name")
            it.heap["${it.heap["\$state_name"]}"] = ActionState<Char>("${it.heap["\$state_name"]}", true)
        }
        val e5 = ActionState<String>("E5-def-body") { it.pop(); it.pop("\$state_name") }
        val e6 = ActionState<String>("E6-def-body")
        val e8 = ActionState<String>("E8-def-body-final") { it.pop("\$is_final"); }
        val e9 = ActionState<String>("E9-def-body-final-end")
        val e9_1 = ActionState<String>("E9.1")
        val e10 = ActionState<String>("E10-def-body-op") { it.add() }
        val e10_1 = ActionState<String>("E10.1") { it.add() }
        val e11 = ActionState<String>("E11-def-body-op") {
            it.pop(); it.pop("\$operation") }
        val e11_1 = ActionState<String>("E11-def-op-end_line")
        val e11_2 = ActionState<String>("E11-def-body-op") { it.pop(); it.pop("\$operation") }
        val e12 = ActionState<String>("E12-def-body-op-end_line")
        val e13 = ActionState<String>("E13-def-body-no_input")
        val e14 = ActionState<String>("E14-def-body-no_input-end_line")
        val e15 = ActionState<String>("E15-def-body-end") {
            it.heap["${it.heap["\$state_name"]}"] =
                    ActionState<Char>("${it.heap["\$state_name"]}", it.heap["\$is_final"] == "F",
                            compileOperation("${it.heap["\$operation"]}"))
            it.heap.remove("\$operation")
            it.heap.remove("\$is_final")
        }
        val e17 = ActionState<String>("E17-def-body-end_line")
        val e18 = ActionState<String>("E18-declarations-end", true)
        val e19 = ActionState<String>("E19")
        val e20 = ActionState<String>("E20-declarations-end") { it.add() }
        val e21 = ActionState<String>("E21-transitions") { it.pop(); it.pop("\$e0") }
        val e22 = ActionState<String>("E22-transitions-name1") { it.pop("\$char") }
        val e22_1 = ActionState<String>("E22-1")
        val e22_2 = ActionState<String>("E22-2") {
            it.heap["\$chars"] = "${it.heap["\$char"]}"[0].."${it.stack.pop()}"[0] }
        val e23 = ActionState<String>("E23-transitions-name1")
        val e23_1 = ActionState<String>("E23.1") { it.pop() }
        val e24 = ActionState<String>("E24-transitions-parameter")
        val e24_1 = ActionState<String>("E24.1") { it.pop(); it.pop("\$e0") }
        val e25 = ActionState<String>("E25-transitions-parameter")
        val e25_1 = ActionState<String>("E25.1")
        val e26 = ActionState<String>("E26-transitions-parameter")
        val e26_1 = ActionState<String>("E26.1")
        val e27 = ActionState<String>("E27") { it.add() }
        val e27_1 = ActionState<String>("E27.1") { it.add() }
        val e28 = ActionState<String>("E28-transitions-end", true) {
            it.pop()
            val stateName = it.stack.pop()
            val state = it.heap[stateName] as State<String>? ?: throw IllegalArgumentException("$stateName is not a defined state")
            if(it.heap["\$chars"] is CharRange) {
                for(c in (it.heap["\$chars"] as CharRange))
                    (it.heap["${it.heap["\$e0"]}"] as State<String>).transitions +=
                            ("$c" to state)
                it.heap.remove("\$chars")
            }
            else
                (it.heap["${it.heap["\$e0"]}"] as State<String>).transitions +=
                        (it.heap["\$char"] as String to state)
        }
        val e28_1 = ActionState<String>("E28.1", true) {
            it.pop()
            (it.heap["${it.heap["\$e0"]}"] as ActionState<String>).nextState = it.heap[it.stack.pop()] as State<String>
        }
    }

    init {
        O.e0.transitions = mutableMapOf("[" to O.e1)

        O.e1.transitions = mutableMapOf("\n" to O.e2, "\r" to O.e1)

        for (c in 'a'..'z') O.e2.transitions += "$c" to O.e3

        O.e3.transitions = mutableMapOf("{" to O.e5)
        for (c in 'a'..'z') O.e3.transitions += "$c" to O.e4
        for (c in 'A'..'Z') O.e3.transitions += "$c" to O.e4
        for (c in '0'..'9') O.e3.transitions += "$c" to O.e4

        O.e4.transitions = mutableMapOf("{" to O.e5, "\r" to O.e4_1, "\n" to O.e4_1, " " to O.e4_1, "\t" to O.e4_1, "_" to O.e4, "(" to O.e4_4)
        for (c in 'a'..'z') O.e4.transitions += "$c" to O.e4
        for (c in 'A'..'Z') O.e4.transitions += "$c" to O.e4
        for (c in '0'..'9') O.e4.transitions += "$c" to O.e4

        O.e4_1.transitions = mutableMapOf("{" to O.e5, "\r" to O.e4_1, "\n" to O.e4_1, " " to O.e4_1, "\t" to O.e4_1, "]" to O.e4_3)
        for (c in 'a'..'z') O.e4_1.transitions += "$c" to O.e4_2

        O.e4_2.nextState = O.e3

        O.e4_3.nextState = O.e17

        O.e4_4.transitions = mutableMapOf("F" to O.e4_5)

        O.e4_5.transitions = mutableMapOf(")" to O.e4_1)

        O.e5.transitions = mutableMapOf("\n" to O.e6, "\r" to O.e6, " " to O.e6)

        O.e6.transitions = mutableMapOf("\n" to O.e6, "F" to O.e8)
        for (c in 'a'..'z') O.e6.transitions += "$c" to O.e9_1

        O.e8.transitions = mutableMapOf("\n" to O.e9, "\r" to O.e9)

        O.e9.transitions = mutableMapOf("\n" to O.e9, "0" to O.e12, "1" to O.e12, "}" to O.e14)
        for (c in 'a'..'z') O.e9.transitions += "$c" to O.e9_1

        O.e9_1.transitions = mutableMapOf(" " to O.e10, "\r" to O.e11, "\n" to O.e11)
        for (c in 'a'..'z') O.e9_1.transitions += "$c" to O.e10
        for (c in 'A'..'Z') O.e9_1.transitions += "$c" to O.e10
        for (c in '0'..'9') O.e9_1.transitions += "$c" to O.e10

        O.e10.transitions = mutableMapOf(" " to O.e10, "\r" to O.e11, "\n" to O.e11, "}" to O.e11_2, "(" to O.e10_1)
        for (c in '*'..'z') O.e10.transitions += "$c" to O.e10

        O.e10_1.transitions = mutableMapOf(")" to O.e10)
        for (c in 'a'..'z') O.e10_1.transitions += "$c" to O.e10_1
        for (c in 'A'..'Z') O.e10_1.transitions += "$c" to O.e10_1
        for (c in '0'..'9') O.e10_1.transitions += "$c" to O.e10_1

        O.e11.nextState = O.e11_1

        O.e11_1.transitions = mutableMapOf("\r" to O.e11_1, "\n" to O.e11_1, "}" to O.e14)

        O.e11_2.nextState = O.e14

        O.e12.transitions = mutableMapOf("\n" to O.e13, "\r" to O.e13)

        O.e13.transitions = mutableMapOf("\n" to O.e13, "}" to O.e14)

        O.e14.transitions = mutableMapOf("\n" to O.e15, "\r" to O.e14, " " to O.e15)

        O.e15.transitions = O.e2.transitions + ("]" to O.e17)

        O.e17.transitions = mutableMapOf("\n" to O.e18, "\r" to O.e18)

        O.e18.transitions = mutableMapOf("\n" to O.e18)
        for (c in 'a'..'z') O.e18.transitions += "$c" to O.e19

        O.e19.transitions = mutableMapOf("(" to O.e21, "=" to O.e24_1)
        for (c in 'a'..'z') O.e19.transitions += "$c" to O.e20
        for (c in 'A'..'Z') O.e19.transitions += "$c" to O.e20
        for (c in '0'..'9') O.e19.transitions += "$c" to O.e20

        O.e20.transitions = mutableMapOf("(" to O.e21, "=" to O.e24_1, "_" to O.e20)
        for (c in 'a'..'z') O.e20.transitions += "$c" to O.e20
        for (c in 'A'..'Z') O.e20.transitions += "$c" to O.e20
        for (c in '0'..'9') O.e20.transitions += "$c" to O.e20

        for (c in '('..'z') O.e21.transitions += "$c" to O.e22

        O.e22.transitions = mutableMapOf(")" to O.e23, "-" to O.e22_1)

        for (c in '('..'z') O.e22_1.transitions += "$c" to O.e22_2

        O.e22_2.transitions = mutableMapOf(")" to O.e23)

        O.e23.transitions = mutableMapOf("=" to O.e24, " " to O.e23_1)

        O.e23_1.transitions = mutableMapOf(" " to O.e23_1, "=" to O.e24)

        O.e24.transitions = mutableMapOf(">" to O.e25)

        O.e24_1.transitions = mutableMapOf(">" to O.e25_1)

        O.e25.transitions = mutableMapOf(" " to O.e25)
        for (c in 'a'..'z') O.e25.transitions += "$c" to O.e26

        O.e25_1.transitions = mutableMapOf(" " to O.e25_1)
        for (c in 'a'..'z') O.e25_1.transitions += "$c" to O.e26_1

        O.e26.transitions = mutableMapOf("\n" to O.e28, "\r" to O.e28, "\u001a" to O.e28)
        for (c in 'a'..'z') O.e26.transitions += "$c" to O.e27
        for (c in 'A'..'Z') O.e26.transitions += "$c" to O.e27
        for (c in '0'..'9') O.e26.transitions += "$c" to O.e27

        O.e26_1.transitions = mutableMapOf("\n" to O.e28_1, "\r" to O.e28_1, "\u001a" to O.e28_1)
        for (c in 'a'..'z') O.e26_1.transitions += "$c" to O.e27_1
        for (c in 'A'..'Z') O.e26_1.transitions += "$c" to O.e27_1
        for (c in '0'..'9') O.e26_1.transitions += "$c" to O.e27_1

        O.e27.transitions = mutableMapOf("\n" to O.e28, "\r" to O.e28, "\u001a" to O.e28, "_" to O.e27)
        for (c in 'a'..'z') O.e27.transitions += "$c" to O.e27
        for (c in 'A'..'Z') O.e27.transitions += "$c" to O.e27
        for (c in '0'..'9') O.e27.transitions += "$c" to O.e27

        O.e27_1.transitions = mutableMapOf("\n" to O.e28_1, "\r" to O.e28_1, "\u001a" to O.e28_1, "_" to O.e27_1)
        for (c in 'a'..'z') O.e27_1.transitions += "$c" to O.e27_1
        for (c in 'A'..'Z') O.e27_1.transitions += "$c" to O.e27_1
        for (c in '0'..'9') O.e27_1.transitions += "$c" to O.e27_1

        O.e28.nextState = O.e18
        O.e28_1.nextState = O.e18

        compiler = StateMachine(O::class.memberProperties.map { it.get(O) }.filter { it is State<*> } as List<State<String>>)
    }

    fun compileStateMachine(fileName: String): StateMachine<String>? {
        val analyzer = Analyser(compiler)
        if(analyzer.analyse(Scanner(File(fileName)).useDelimiter(""))) {
            println("L'automate a été compilé avec succès")
            val e0 = analyzer.stateMachine.machine.heap["e0"]
            if(e0 is State<*>) {
                return StateMachine.from(e0) as StateMachine<String>
            }
        } else {
            println("Une erreur est survenue lors de la compilation.")
            analyzer.stateMachine.log.forEach { println(it) }
        }
        return null
    }
}