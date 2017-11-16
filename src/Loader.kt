import java.nio.file.Files
import java.nio.file.Paths

fun readFile(loc: String): State<Char>? {
  val matrix: MutableList<List<Char>> = mutableListOf()
  Files.newBufferedReader(Paths.get(loc)).use {
	while(true) {
		val line = it.readLine() ?: return@use
		val transitions: MutableList<Char> = mutableListOf()
		for (transition in line.split(','))
			transitions += transition[0]
		matrix += transitions
	}
  }
  println(matrix)
  val states = StateFactory(*matrix.toTypedArray()) 
  return if (states.isEmpty()) null else states[0]
}
