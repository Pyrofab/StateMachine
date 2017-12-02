@file:JvmName("OperationCompiler")

package statemachine.machines

fun compileOperation(op: String): ((Machine) -> Unit) {
    val args = op.split(" ")
    return when(args[0]) {
        "unbox" -> { m:Machine -> m.unbox() }
        "exec" -> { m -> compileOperation("${m.stack.pop()}")(m) }
        "pop" -> { m -> if(args.size > 1) m.pop(args[1]) else m.pop() }
        "push" -> { m -> m.push(args[1])}
        "mov" -> { m -> m.mov(args[1], args[2]) }
        "add" -> { m -> m.add() }
        "sub" -> { m -> m.sub() }
        "mul" -> { m -> m.mul() }
        "div" -> { m -> m.div() }
        else -> { _ -> }
    }
}
