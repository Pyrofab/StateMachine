import java.io.File
import java.io.FileNotFoundException
import java.nio.file.NoSuchFileException
import java.util.Arrays

fun main(args: Array<String>) {
  while(true) {
    println("Enter a predefined state machine to run or \"file\" to load your own:")
    when (readLine()) {
      "smileys" -> testSmiley()
      "hours" -> testHour()
      "mails" -> testMail()
      "file" -> loadFile()
      "action" -> testActionStateMachine()
      "double" -> testDoubleFinal()
      "compiler" -> testCompilerStateMachine()
      "quit" -> return
      else -> println("Commande non définie. Les commandes valides sont \"smileys\", \"hours\", \"mails\", \"file\", \"action\", \"double\", \"quit\"")
    }
  }
}

fun testHour() {
  println("==========hours==========")
  val tests = listOf("00:00", "48:12", "06:89", "23:59")
  val e0 = State<Char>("E0")
  val h1 = State<Char>("H1")
  val h2 = State<Char>("H2")
  val h = State<Char>("H")
  val m1 = State<Char>("M1")
  val m2 = State<Char>("M2")
  val m = State<Char>("M", true)
  e0.transitions = mapOf('0' to h1, '1' to h1, '2' to h2)
  h1.transitions = fillMap(mutableMapOf(), "0123456789", h)
  h2.transitions = fillMap(mutableMapOf(), "0123", h)
  h.transitions = mapOf(':' to m1)
  m1.transitions = fillMap(mutableMapOf(), "012345", m2)
  m2.transitions = fillMap(mutableMapOf(), "0123456789", m)
  val analyseur = StringAnalyser(listOf(e0, h1, h2, h, m1, m2))
  tests.forEach {println(analyseur.analyse(it))}
}

fun testSmiley() {
  println("=========smileys=========")
  val tests = listOf(";-)", ":=)", ";-(", ";=)")
  val e0 = State<Char>("E0")
  val e1 = State<Char>("E1")
  val e2 = State<Char>("E2")
  val e3 = State<Char>("E3")
  val e4 = State<Char>("E4", true)
  e0.transitions = mapOf(':' to e1, ';' to e2)
  e1.transitions = mapOf('-' to e3, '=' to e3)
  e2.transitions = mapOf('-' to e3)
  e3.transitions = mapOf(')' to e4, '(' to e4)
  val analyseur = StringAnalyser(listOf(e0, e1, e2, e3, e4))
  tests.forEach {println(analyseur.analyse(it))}
}

fun loadFile() {
  println("========readFile=========")
  try {
    println("Please input the name of your state machine file:")
    val analyseur = StringAnalyser(readFile("data/${readLine()}"))
    println("State machine loaded as : $analyseur")
    println("Please input the name of your test file:")
    val tests = readTestFile("data/${readLine()}")
    tests.forEach {println(analyseur.analyse(it))}
  } catch (e: NoSuchFileException) {
    println("The provided file has not been found. Currently existing files are : ${Arrays.toString(File("data").listFiles())}")
  }
}

fun testDoubleFinal() {
  println("==========doubleFinal============")
  val e0 = State<Char>("E0")
  val e1 = State<Char>("E1", true)
  val e2 = ActionState<Char>("E2", true, {_ -> println("slt ca marche")})
  e0.transitions = mapOf('a' to e1)
  e1.transitions = mapOf('a' to e2)
  val anal = StringAnalyser(listOf(e0, e1, e2))
  println(anal.analyse("aa"))
}

fun testActionStateMachine() {
  println("======actionState========")
  val e0 = State<Char>("E0")
  val e1 = ActionState<Char>("E0", op={it.heap["var1"] = 3})
  val e2 = ActionState<Char>("E1", true, {println(it.heap["var1"])})
  e0.transitions = mapOf('*' to e1)
  e1.transitions = mapOf('+' to e2)
  val analyseur = StringAnalyser(listOf(e0, e1, e2))
  println(analyseur.analyse("*+"))
}

fun testCompilerStateMachine() {
  println("=========Assign=========")
  val e0 = State<Char>("E0")
  val e1 = ActionState<Char>("E1", op={it.pop("assigned")})
  val e2 = State<Char>("E2")
  val e3 = ActionState<Char>("E3", true, op={it.unbox(); it.pop("var1")})
  val e31 = ActionState<Char>("E3.1", true, op={it.pop("var1")})
  val e4 = State<Char>("E+")
  val e5 = State<Char>("E-")
  val e6 = ActionStateNoInput<Char>("E6", op={it.push("var1"); it.print(); it.unbox(); it.push("var1"); it.unbox(); it.add(); it.pop("var1")})
  val e7 = ActionStateNoInput<Char>("E7", op={it.unbox(); it.push("var1"); it.unbox(); it.sub(); it.pop("var1")})
  val e8 = ActionState<Char>("E8", true, op={it.push("var1"); it.print()})
  val e9 = ActionState<Char>("E9", true, op={it.push("assigned"); it.unbox(); it.mov("var1", null); println(it.heap);})
  e0.transitions = fillMap(mutableMapOf(), "vxy", e1)
  e1.transitions = mutableMapOf('=' to e2)
  e2.transitions = fillMap(fillMap(mutableMapOf(), "vxy", e3), "0123456789", e31)
  e3.transitions = mutableMapOf('+' to e4, '-' to e5, ';' to e9)
  e31.transitions = e3.transitions
  e4.transitions = fillMap(mutableMapOf(), "vxy", e6)
  e5.transitions = fillMap(mutableMapOf(), "vxy", e7)
  e6.nextState = e8
  e7.nextState = e8
  e8.transitions = e3.transitions
  e9.transitions = e0.transitions
  val analyseur = StringAnalyser(listOf(e0,e1,e2,e3,e4,e5,e6,e7,e8))
  while(analyseur.analyse(readLine() ?: ""));
}

fun testMail() {
  println("==========mails==========")
  val tests = listOf("truc.truc@univ-lyon.fr", "abc", "null@null", "null@null.com")
  val e0 = State<Char>("E0")
  val e1 = State<Char>("E1")
  val e2 = State<Char>("E2")
  val e3 = State<Char>("E3", true)
  val alphaNumeric = "abcdefghijklmnopqrstuvxyz0123456789-_"
  e0.transitions = fillMap(mutableMapOf('.' to e1, '@' to e2), alphaNumeric, e0)
  e1.transitions = fillMap(mutableMapOf('@' to e2), alphaNumeric, e1)
  e2.transitions = fillMap(mutableMapOf('.' to e3), alphaNumeric, e2)
  val analyseur = StringAnalyser(listOf(e0, e1, e2, e3))
  tests.forEach {println(analyseur.analyse(it))}
}

fun fillMap(map: MutableMap<Char, State<Char>>, possibilities: String, result: State<Char>): MutableMap<Char, State<Char>> {
  for(c in possibilities)
  map += c to result
  return map
}
