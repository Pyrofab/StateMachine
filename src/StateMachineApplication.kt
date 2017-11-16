
fun main(args: Array<String>) {
  while(true) {
	when (readLine()) {
		"smileys" -> testSmiley()
		"hours" -> testHour()
		"mails" -> testMail()
		"file" -> testSmiley2()
		"quit" -> return
	}
  }/*
  println("=========smileys=========")
  testSmiley()
  println("==========hours==========")
  testHour()
  println("==========mails==========")
  testMail()
  println("========readFile=========")
  testSmiley2()*/
}

fun testHour() {
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
  val analyseur = StringAnalyser(e0)
  tests.forEach {println(analyseur.analyse(it))}
}

fun testSmiley() {
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
  val analyseur = StringAnalyser(e0)
  tests.forEach {println(analyseur.analyse(it))}
}

fun testSmiley2() {
  val tests = listOf(";-)", ":=)", ";-(", ";=)")
  val analyseur = StringAnalyser(readFile("data/test.csv") ?: return)
  tests.forEach {println(analyseur.analyse(it))}
}

fun testMail() {
  val tests = listOf("truc.truc@univ-lyon.fr", "abc", "null@null", "null@null.com")
  val e0 = State<Char>("E0")
  val e1 = State<Char>("E1")
  val e2 = State<Char>("E2")
  val e3 = State<Char>("E3", true)
  val alphaNumeric = "abcdefghijklmnopqrstuvxyz0123456789-_"
  e0.transitions = fillMap(mutableMapOf('.' to e1, '@' to e2), alphaNumeric, e0)
  e1.transitions = fillMap(mutableMapOf('@' to e2), alphaNumeric, e1)
  e2.transitions = fillMap(mutableMapOf('.' to e3), alphaNumeric, e2)
  val analyseur = StringAnalyser(e0)
  tests.forEach {println(analyseur.analyse(it))}
}

fun fillMap(map: MutableMap<Char, State<Char>>, possibilities: String, result: State<Char>): Map<Char, State<Char>> {
 for(c in possibilities)
   map += c to result
 return map
}
