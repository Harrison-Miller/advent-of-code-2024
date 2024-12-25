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
        "input" to 52866066286310,
    ))
}

private fun part1(lines: List<String>): Long {
    val input = readInput(lines)
    return run(input)
}

// swap 1 qdg,z12
// swap 2 vvf,z19
// swap 3 fgn,dck (z23)
// swap 4 nvh,z37
// dck,fgn,nvh,qdg,vvf,z12,z19,z37
private fun part2(lines: List<String>): Long {
    val input = readInput(lines)
    val x = input.wires.filterKeys { it.startsWith('x') }.toList().toLong()
    val y = input.wires.filterKeys { it.startsWith('y') }.toList().toLong()
    val goal = y + x

    println("goal: $goal")


    // this is the carry bit of the half adder
    // which we manually checked
    val carrybits = listOf("gct")
    input.validateFullAdders(carrybits)

    return run(input)
}

private fun Input.validateFullAdders(carrybits: List<String>, depth: Int = 1) {
    if (depth == 45) {
        return
    }
    val xbit = if (depth < 10) "x0$depth" else "x$depth"
    val ybit = if (depth < 10) "y0$depth" else "y$depth"
    val zbit =  if (depth < 10) "z0$depth" else "z$depth"
    val pcbit = carrybits.get(depth-1)

    val xxorygate = gates.find { it.lhs in listOf(xbit, ybit) && it.rhs in listOf(xbit, ybit) && it.op == "XOR" }!! // should alwats exust
    val outgate = gates.find { it.out == zbit }!! // should always exist

    if (outgate.op != "XOR") {
        println("wrong type of gate set to output to $zbit: expected: XOR, got: $outgate")
    }

    val desiredOutgateIn = setOf(xxorygate.out, pcbit)

    if (!(outgate.lhs in desiredOutgateIn && outgate.rhs in desiredOutgateIn)) {
        val from = desiredOutgateIn - setOf(outgate.lhs, outgate.rhs)
        val to = setOf(outgate.lhs, outgate.rhs) - desiredOutgateIn
        println("out gate for $zbit not configured correctly: $outgate")
        println("swap: $from and $to")
    }

    val lcarrygate = gates.find { it.lhs in desiredOutgateIn && it.rhs in desiredOutgateIn && it.op == "AND" }
    val rcarrygate = gates.find { it.lhs in listOf(xbit, ybit) && it.rhs in listOf(xbit, ybit) && it.op == "AND" }!! // should always exist
    val rcbit = rcarrygate.out
    val carrygate = if (lcarrygate == null) {
        println("left carry gate not found should be: $desiredOutgateIn AND")
        gates.find { (it.lhs == rcbit || it.rhs == rcbit) && it.op == "OR" }
    } else {
        gates.find { (it.lhs in listOf(rcbit, lcarrygate.op) || it.rhs in listOf(rcbit, lcarrygate.out)) && it.op == "OR" }
    }

    println("found carry gate for depth: $depth, $carrygate")

    val nextCarryBits = if(carrygate != null) carrybits + carrygate.out else carrybits
    validateFullAdders(nextCarryBits, depth+1)
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

private fun run(input: Input): Long {
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