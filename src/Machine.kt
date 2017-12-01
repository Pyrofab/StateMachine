import java.util.*
import java.util.regex.*

class Machine(var heap: MutableMap<String, Any> = mutableMapOf(), var stack: Deque<Any> = LinkedList()) {
  val POINTER = "\\((.*)\\)".toRegex().toPattern()

  fun unbox() {
    stack.push(unbox("(${stack.pop()})"))
  }

  fun unbox(pointer: String): String {
    val matcher = POINTER.matcher(pointer)
    if(matcher.matches())
      return matcher.group(1)
    return pointer
  }

  fun pop(varName: String) {
    heap[varName] = stack.pop()
  }

  fun push(varName: String) {
    stack.push(heap[varName])
  }

  fun mov(src: String, dest: String?) {
    val value = heap[src]
    if(value != null) {
      heap[dest ?: "${stack.pop()}"] = value
    } else heap.remove(dest)
  }

  fun print() = println(stack.pop())

  fun add() {
    var i1 = stack.pop()
    var i2 = stack.pop()
    if(i1 is String) i1 = i1.toIntOrNull() ?: i1
    if(i2 is String) i2 = i2.toIntOrNull() ?: i2
    if(i1 is String || i2 is String)
      stack.push("$i1$i2")
    else if (i1 is Int && i2 is Int)
      stack.push(i1 + i2)
    else if (i1 is Number && i2 is Number)
      stack.push(i1.toDouble() + i2.toDouble())
    else throw UnsupportedOperationException("$i1 and $i2 are not known operands")
  }

  fun sub() {
    var i1 = stack.pop()
    var i2 = stack.pop()
    if(i1 is String) i1 = i1.toIntOrNull() ?: i1
    if(i2 is String) i2 = i2.toIntOrNull() ?: i2
    if (i1 is Int && i2 is Int)
      stack.push(i1 - i2)
    else if (i1 is Number && i2 is Number)
      stack.push(i1.toDouble() - i2.toDouble())
    else throw UnsupportedOperationException("$i1(${i1.javaClass.simpleName}) and $i2(${i2.javaClass.simpleName}) are not known operands")
  }

  fun mul() {
    var i1 = stack.pop()
    var i2 = stack.pop()
    if(i1 is String) i1 = i1.toIntOrNull() ?: i1
    if(i2 is String) i2 = i2.toIntOrNull() ?: i2
    if (i1 is Int && i2 is Int)
      stack.push(i1 * i2)
    else if (i1 is Number && i2 is Number)
      stack.push(i1.toDouble() * i2.toDouble())
    else throw UnsupportedOperationException("$i1(${i1.javaClass.simpleName}) and $i2(${i2.javaClass.simpleName}) are not known operands")
  }

  fun div() {
    var i1 = stack.pop()
    var i2 = stack.pop()
    if(i1 is String) i1 = i1.toIntOrNull() ?: i1
    if(i2 is String) i2 = i2.toIntOrNull() ?: i2
    if (i1 is Int && i2 is Int)
      stack.push(i1 / i2)
    else if (i1 is Number && i2 is Number)
      stack.push(i1.toDouble() / i2.toDouble())
    else throw UnsupportedOperationException("$i1(${i1.javaClass.simpleName}) and $i2(${i2.javaClass.simpleName}) are not known operands")
  }
}
