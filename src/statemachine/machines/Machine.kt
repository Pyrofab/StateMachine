package statemachine.machines

import java.util.*

/**
 * Classe décrivant une machine virtuelle primitive
 *
 * [heap] le tas de la machine virtuelle, dans lequel les automates peuvent enregistrer des variables
 * [stack] la pile de la machine virtuelle, utilisée par les automates pour réaliser des opérations simples
 *
 * @author Fabien
 */
class Machine(var heap: MutableMap<String, Any> = mutableMapOf(), var stack: Deque<Any> = LinkedList()) {
    companion object {
        val POINTER = "\\((.*)\\)".toRegex().toPattern()
    }

    /**
     * Tente de remplacer la chaîne de caractères présente dans la pile par le contenu de la variable correspondante dans le tas
     */
    fun unbox() {
        stack.push(unbox("(${stack.pop()})"))
    }

    private fun unbox(pointer: String): String {
        val matcher = POINTER.matcher(pointer)
        if (matcher.matches())
            return "${heap[matcher.group(1)]}"
        return pointer
    }

    fun pop() = pop("null")

    /**
     * Dépile et stock le premier élément de la pile dans une variable du tas dont le nom correspond à la chaîne passée en paramètre
     *
     * [varName] le nom de la variable dans laquelle le contenu de la pile sera stocké
     */
    fun pop(varName: String) {
        if(varName != "null")
            heap[varName] = stack.pop()
        else stack.pop()
    }

    /**
     * Ajoute le contenu de la variable passée en paramètre au sommet de la pile
     *
     * [varName] le nom de la variable dont le contenu sera ajouté à la pile
     */
    fun push(varName: String) {
        if(varName != "null")
            stack.push(heap[varName])
    }

    /**
     * Déplace le contenu de la variable source dans celui de la variable destination
     *
     * [src] le nom de la variable dont le contenu sera copié dans la variable de destination
     * [dest] le nom d'une variable dans laquelle le contenu de la source sera enregistré. Si cette valeur est nulle,
     * enregistre dans la variable dont le nom correspond à l'élément sommet de la pile
     */
    fun mov(src: String, dest: String?) {
        val value = heap[src]
        if (value != null && dest != "null") {
            heap[dest ?: "${stack.pop()}"] = value
        } else heap.remove(dest)
    }

    /**
     * Dépile le premier élément de la pile et l'affiche
     */
    fun print() = println(stack.pop())

    /**
     * Dépile les deux premiers éléments de la pile et réempile leur somme
     *
     * *Règles d'opération :*
     * Les chaînes de caractères sont d'abord converties en entier si possible.
     * Après cette conversion, si l'un des deux éléments reste une chaîne de caractères, les deux éléments sont concaténés.
     * Si les deux éléments sont des nombres, ils sont additionnés. Sinon, une exception est lancée
     *
     * @throws UnsupportedOperationException si les deux objets à additionner ne sont pas pris en charge par la machine
     */
    fun add() {
        var i2 = stack.pop()
        var i1 = stack.pop()
        if (i1 is String) i1 = i1.toIntOrNull() ?: i1
        if (i2 is String) i2 = i2.toIntOrNull() ?: i2
        if (i1 is String || i2 is String)
            stack.push("$i1$i2")
        else if (i1 is Int && i2 is Int)
            stack.push(i1 + i2)
        else if (i1 is Number && i2 is Number)
            stack.push(i1.toDouble() + i2.toDouble())
        else throw UnsupportedOperationException("$i1 and $i2 are not known operands")
    }

    /**
     * Dépile les deux premiers éléments de la pile et réempile leur différence
     *
     * *Règles d'opération :*
     * Les chaînes de caractères sont d'abord converties en entier si possible.
     * Si les deux éléments sont des nombres, le deuxième est soustrait au premier. Sinon, une exception est lancée
     *
     * @throws UnsupportedOperationException si les deux objets à soustraire ne sont pas pris en charge par la machine
     */
    fun sub() {
        var i2 = stack.pop()
        var i1 = stack.pop()
        if (i1 is String) i1 = i1.toIntOrNull() ?: i1
        if (i2 is String) i2 = i2.toIntOrNull() ?: i2
        if (i1 is Int && i2 is Int)
            stack.push(i1 - i2)
        else if (i1 is Number && i2 is Number)
            stack.push(i1.toDouble() - i2.toDouble())
        else throw UnsupportedOperationException("$i1(${i1.javaClass.simpleName}) and $i2(${i2.javaClass.simpleName}) are not known operands")
    }

    /**
     * Dépile les deux premiers éléments de la pile et réempile leur produit
     *
     * *Règles d'opération :*
     * Les chaînes de caractères sont d'abord converties en entier si possible.
     * Si les deux éléments sont des nombres, ils sont multipliés entre eux. Sinon, une exception est lancée
     *
     * @throws UnsupportedOperationException si les deux objets à multiplier ne sont pas pris en charge par la machine
     */
    fun mul() {
        var i2 = stack.pop()
        var i1 = stack.pop()
        if (i1 is String) i1 = i1.toIntOrNull() ?: i1
        if (i2 is String) i2 = i2.toIntOrNull() ?: i2
        if (i1 is Int && i2 is Int)
            stack.push(i1 * i2)
        else if (i1 is Number && i2 is Number)
            stack.push(i1.toDouble() * i2.toDouble())
        else throw UnsupportedOperationException("$i1(${i1.javaClass.simpleName}) and $i2(${i2.javaClass.simpleName}) are not known operands")
    }

    /**
     * Dépile les deux premiers éléments de la pile et réempile leur quotient
     *
     * *Règles d'opération* :
     * Les chaînes de caractères sont d'abord converties en entier si possible.
     * Si les deux éléments sont des nombres, le premier est divisé par le deuxième. Sinon, une exception est lancée
     *
     * @throws UnsupportedOperationException si les deux objets à diviser ne sont pas pris en charge par la machine
     */
    fun div() {
        var i2 = stack.pop()
        var i1 = stack.pop()
        if (i1 is String) i1 = i1.toIntOrNull() ?: i1
        if (i2 is String) i2 = i2.toIntOrNull() ?: i2
        if (i1 is Int && i2 is Int)
            stack.push(i1 / i2)
        else if (i1 is Number && i2 is Number)
            stack.push(i1.toDouble() / i2.toDouble())
        else throw UnsupportedOperationException("$i1(${i1.javaClass.simpleName}) and $i2(${i2.javaClass.simpleName}) are not known operands")
    }

    override fun toString(): String = "Machine(heap=$heap,\n stack=$stack)"


}
