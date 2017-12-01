import java.nio.file.Files
import java.nio.file.Paths

fun readFile(loc: String): List<State<Char>> {
    println("Loading $loc as state machine")
    val matrix: MutableList<List<Char>> = mutableListOf()
    Files.newBufferedReader(Paths.get(loc)).use {
        while (true) {
            val line = it.readLine() ?: return@use
            val transitions: MutableList<Char> = mutableListOf()
            for (transition in line.split(','))
                transitions += transition[0]
            matrix += transitions
        }
    }
    println(matrix)
    return State.create(*matrix.toTypedArray())
}

fun readTestFile(loc: String): List<String> {
    println("Loading $loc as test file")
    val ret: MutableList<String> = mutableListOf()
    Files.newBufferedReader(Paths.get(loc)).use {
        val line = it.readLine() ?: return@use
        for (transition in line.split(','))
            ret += transition
    }
    return ret
}
