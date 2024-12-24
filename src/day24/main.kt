package day24

import utils.*

/*
half adder would be
xn xor yn-> zn
xn and yn -> carryn

luckily for us xor can not be recreated from other gates without not

full adder would be
xn xor yn -> subn
carryn-1 xor subn -> zn
carryn-1 and subn -> leftcarryn
xn and yn -> rightcarryn
leftcarryn or rightcarryn -> carryn

I did some manual work and it definitely appears to be a ripple carry adder.

the inputs are 45 bits long
we have 222 gates

a half adder is 2 gates (which we used)
a full adder is 5 gates, giving us exactly 44 full adders
we have z45 so the output is 46 bits accounting for the carry.

for each pair of inputs build out an adder class, labeling each gates output.
Then check that each gate is using the expected output within the adder.
Check the carry previous

if we find something wrong log it for now.
 */

fun main() {
    val day = packageName{}
//    ::part1.runTests(day, listOf(
//        "test" to 2024,
//        "input" to 52728619468518,
//    ))

    ::part2.runTests(day, listOf(
        "input" to null,
    ))
}

private fun part1(lines: List<String>): Long {
    val input = readInput(lines)
    return runAdder(input)
}

private fun part2(lines: List<String>): Int {
    val input = readInput(lines)
    val x = input.wires.filterKeys { it.startsWith('x') }.toList().toLong()
    val y = input.wires.filterKeys { it.startsWith('y') }.toList().toLong()
    val goal = y + x

    println("goal: $goal")

    return 0
}

private data class Gate(
    val lhs: String,
    val op: String,
    val rhs: String,
    val out: String,
)

private fun Gate.run(wires: Map<String, Int>): Int {
    val l = wires[lhs]
    check(l != null) { "tried to run $this, but $lhs was null" }
    val r = wires[rhs]
    check(r != null) { "tried to run $this, but $rhs was null" }

    return when(op) {
        "AND" -> l and r
        "OR" -> l or r
        "XOR" -> l xor r
        else -> 0
    }
}

private fun Gate.ready(wires: Map<String, Int>): Boolean {
    val l = wires[lhs]
    val r = wires[rhs]
    return l != null && r != null
}

private data class Input(
    val wires: Map<String, Int>,
    val gates: Set<Gate>,
)

private fun readInput(lines: List<String>): Input {
    val (wireLines, gateLines) = lines.map{ it.substringBefore("//").trim() }.partition { it.contains(":") }
    return Input(
        wires = wireLines.map {
            val (name, on) = it.split(":")
            name to on.trim().toInt()
        }.toMap(),

        gates = gateLines.filterNot { it.isEmpty() }.map {
            val (g, out) = it.split("->")
            val (lhs, op, rhs) = it.trim().split(" ")
            Gate(lhs,op,rhs, out.trim())
        }.toSet()
    )
}

private fun runAdder(input: Input): Long {
    var wires = input.wires.toMutableMap()

    val startingGates = input.gates.filter {
        (it.lhs.startsWith('x') || it.lhs.startsWith('y')) &&
                (it.rhs.startsWith('x') || it.rhs.startsWith('y'))
    }.toSet()

    startingGates.floodFill { g ->
        val o = g.run(wires)
        wires[g.out] = o

        input.gates.filter { (it.lhs == g.out || it.rhs == g.out) && it.ready(wires) }.toSet()
    }

    val bits = wires.filterKeys { it.startsWith('z') }.toList().sortedByDescending { it.first }

    return bits.toLong()
}

private fun List<Pair<String, Int>>.toLong(): Long {
    val bits = sortedByDescending { it.first }.map { it.second.toLong() }
    var out = 0L
    bits.forEach { bit ->
        out = out.shl(1)
        out = out or bit
    }
    return out
}