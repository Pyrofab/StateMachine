package statemachine.machines

import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * Classe décrivant une machine virtuelle primitive
 *
 * [heap] le tas de la machine virtuelle, dans lequel les automates peuvent enregistrer des variables
 * [stack] la pile de la machine virtuelle, utilisée par les automates pour réaliser des opérations simples
 *
 * @author Fabien
 */
class Machine(var heap: MutableMap<String?, Any> = Heap(), var stack: Deque<Any> = LinkedList()) {
    companion object {
        val POINTER = "\\((.*)\\)".toRegex().toPattern()
//        val CONSTANT = "\$(.*)".toRegex().toPattern()
        val NULL = "null".toRegex()
    }

    class Heap : LinkedHashMap<String?, Any>() {
        override fun get(key: String?): Any? = if(key == null) null else super.get(key)
    }

    /**
     * Tente de remplacer la chaîne de caractères présente dans la pile par le contenu de la variable correspondante dans le tas
     */
    fun unbox() {
        stack.push("(${stack.pop()})")
    }

    private fun unbox(rawArg: String): String? {
        var matcher = POINTER.matcher(rawArg)
        if (matcher.matches())
            return "${heap[matcher.group(1)]}"
//        matcher = CONSTANT.matcher(rawArg)
//        if(matcher.matches())
//            return matcher.group(1)
        if(NULL.matches(rawArg)) return null
        return rawArg
    }

    fun pop() = pop("null")

    /**
     * Dépile et stock le premier élément de la pile dans une variable du tas dont le nom correspond à la chaîne passée en paramètre
     *
     * [varName] le nom de la variable dans laquelle le contenu de la pile sera stocké
     */
    fun pop(varName: String) {
        val arg = unbox(varName)
        heap[arg] = stack.pop()
    }

    /**
     * Ajoute le contenu de la variable passée en paramètre au sommet de la pile
     *
     * [varName] le nom de la variable dont le contenu sera ajouté à la pile
     */
    fun push(varName: String) {
        val arg = unbox(varName)
        stack.push(arg)
    }

    /**
     * Déplace le contenu de la variable source dans celui de la variable destination
     *
     * [src] le nom de la variable dont le contenu sera copié dans la variable de destination
     * [dest] le nom d'une variable dans laquelle le contenu de la source sera enregistré. Si cette valeur est nulle,
     * enregistre dans la variable dont le nom correspond à l'élément sommet de la pile
     */
    fun mov(src: String, dest: String) {
        val value = unbox(src)
        val destPointer = unbox(dest)
        if(value != null)
            heap[destPointer] = value
        else heap.remove(destPointer)
    }

    /**
     * Dépile le premier élément de la pile et l'affiche
     */
    fun print() = println(unbox("${stack.pop()}"))

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
        val arg2 = unbox("${stack.pop()}") ?: throw NullPointerException("Can't use a null element in an addition")
        val arg1 = unbox("${stack.pop()}") ?: throw NullPointerException("Can't use a null element in an addition")
        val i1 = arg1.toIntOrNull() ?: arg1.toDoubleOrNull() ?: arg1
        val i2 = arg2.toIntOrNull() ?: arg2.toDoubleOrNull() ?: arg2
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
        val arg2 = unbox("${stack.pop()}") ?: throw NullPointerException("Can't use a null element in a subtraction")
        val arg1 = unbox("${stack.pop()}") ?: throw NullPointerException("Can't use a null element in a subtraction")
        val i1 = arg1.toIntOrNull() ?: arg1
        val i2 = arg2.toIntOrNull() ?: arg2
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
        val arg2 = unbox("${stack.pop()}") ?: throw NullPointerException("Can't use a null element in a multiplication")
        val arg1 = unbox("${stack.pop()}") ?: throw NullPointerException("Can't use a null element in a multiplication")
        val i1 = arg1.toIntOrNull() ?: arg1
        val i2 = arg2.toIntOrNull() ?: arg2
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
        val arg2 = unbox("${stack.pop()}") ?: throw NullPointerException("Can't use a null element in a division")
        val arg1 = unbox("${stack.pop()}") ?: throw NullPointerException("Can't use a null element in a division")
        val i1 = arg1.toIntOrNull() ?: arg1
        val i2 = arg2.toIntOrNull() ?: arg2
        if (i1 is Int && i2 is Int)
            stack.push(i1 / i2)
        else if (i1 is Number && i2 is Number)
            stack.push(i1.toDouble() / i2.toDouble())
        else throw UnsupportedOperationException("$i1(${i1.javaClass.simpleName}) and $i2(${i2.javaClass.simpleName}) are not known operands")
    }

    override fun toString(): String = "Machine(heap=$heap,\n stack=$stack)"


}
