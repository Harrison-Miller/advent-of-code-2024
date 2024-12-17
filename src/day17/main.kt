package day17

import utils.*
import kotlin.math.pow

/* Part 2 Observations
The test program and the real input have jump to 0 as the final commands
The programs contain no other jump commands
Therefore the program will only halt when register A is 0 at the end of the program

The second to last command for the test program and the real input is the out command
The programs contain no other out commands

We know that the program will send 0 to the out command on the final iteration.

The out command in both programs uses the combo operand and always uses a register (never a literal)

Test program only uses register A to determine the output, B/C registers are determined by the initial value of A for that iteration

adv/bdv/cdv are equivalent to bitshift right n (n=operand value).

register A is only ever modified with adv 3 (bitshift right 3)

WE ONLY HAVE TO SEARCH REGISTER A WHICH SIGNIFICANTLY REDUCES THE SEARCH SPACE
B/C will always start 0

Turns out It is still a really big search space and the recursive search solution is not quick.
The search space is bigger than Int.MAX_VALUE
 */

fun main() {
    val day = packageName{}
//    ::part1.runTests(day, listOf(
////        "test" to "4,6,3,5,6,3,5,2,1,0",
//        "input" to null,
//    ))

    ::part2.runTests(day, listOf(
        "test2" to 117440,
        "input" to 202322936867370,
    ))
}

private fun part1(lines: List<String>): String {
    var machine = lines.toMachine()
    return machine.run()
}

private fun part2(lines: List<String>): Long {
    var machine = lines.toMachine()
    val a = machine.search()

    check(lines[4].substringAfter("Program: ") == machine.copy(a=a).run()) { "the value found for a doesn't replicate the program" }
    return a
}

private fun List<String>.toMachine(): Machine {
    check(size >= 5) { "machine initialization format expects atleast 5 lines but was: $size"}
    val code = get(4).substringAfter("Program: ").splitToInts(Regex(","))
    return Machine(
        a = get(0).substringAfter("Register A: ").toLong(),
        b = get(1).substringAfter("Register B: ").toLong(),
        c = get(2).substringAfter("Register C: ").toLong(),
        code = (0..code.lastIndex step 2).map {
            code[it] to code[it+1]
        }
    )
}

private data class Machine(
    val a: Long,
    val b: Long,
    val c: Long,
    val code: List<Pair<Int, Int>>,
    val pc: Int = 0,
    val buffer: List<Int> = emptyList(),
    val halted: Boolean = false,
)

private fun Machine.getOutput() = buffer.map { it.toString() }.joinToString(",")

private fun Machine.reset() = copy(pc=0,halted=false)

private fun Machine.run(): String {
    var nm = reset()
    while(!nm.halted) {
        nm = nm.step()
    }
    return nm.getOutput()
}

private fun Machine.step(): Machine {
    if (pc > code.lastIndex || halted) {
        return copy(halted=true)
    }

    val (opcode, operand) = code.get(pc)
    check(opcode in 0..7) { "opcode must be 0..7 was: $opcode"}
    check(operand in 0..7) {"operand must be 0..7 was: $operand"}

    val nextMachine = when (opcode) {
        0 -> adv(operand)
        1 -> bxl(operand)
        2 -> bst(operand)
        3 -> jnz(operand)
        4 -> bxc(operand)
        5 -> out(operand)
        6 -> bdv(operand)
        7 -> cdv(operand)
        else -> this
    }

    if (nextMachine.pc == pc) {
        // increment pc by 1 instead of 2 because of using pairs. This is ok because jnz only ever goes to 0
        return nextMachine.copy(pc = pc+1)
    }
    return nextMachine
}

private fun Machine.comboOperand(operand: Int): Long {
    check(operand != 7) { "combo operand may never be 7"}
    return when (operand) {
        4 -> a
        5 -> b
        6 -> c
        else -> operand.toLong()
    }
}

private fun Machine.adv(operand: Int) = copy(a = (a / 2.0.pow(comboOperand(operand).toDouble())).toLong())
private fun Machine.bxl(operand: Int) = copy(b = b xor operand.toLong())
private fun Machine.bst(operand: Int) = copy(b = comboOperand(operand) % 8)
private fun Machine.jnz(operand: Int) = copy(pc = if(a == 0L) pc else operand) // only ever jumps to 0
private fun Machine.bxc(operand: Int) = copy( b = b xor c)
private fun Machine.out(operand: Int) = copy(buffer = buffer + (comboOperand(operand) % 8).toInt())
private fun Machine.bdv(operand: Int) = copy(b = (a / 2.0.pow(comboOperand(operand).toDouble())).toLong())
private fun Machine.cdv(operand: Int) = copy(c = (a / 2.0.pow(comboOperand(operand).toDouble())).toLong())


typealias MachineFunc = (a: Long) -> Int
private fun Machine.toFunction(): MachineFunc {
    return fun(a: Long): Int {
        val noJump = code - Pair(3, 0)
        var m = copy(a=a, b=0, c=0, pc=0, code=noJump)
        m.code.forEach {
            m = m.step()
        }
        return m.buffer.single()
    }
}

private fun Machine.search(): Long {
    val f = toFunction()
    val code = code.map {
        listOf(it.first, it.second)
    }.flatten()
    return code.search(f, 0, code.lastIndex) ?: 0
}

private fun List<Int>.search(f: MachineFunc, a: Long, index: Int): Long? {
    if (index < 0) {
        return a/8
    }

    val targetOut = get(index)
    (0..7).forEach {
        val nextA = a+it
        val out = f(nextA)
        if (out == targetOut) {
            val finalA = search(f, nextA*8, index-1)
            if (finalA != null) {
                return finalA
            }
        }
    }

    return null
}
