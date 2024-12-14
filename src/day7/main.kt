package day7

import utils.*

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 3749,
        "input" to 932137732557,
    ))

    ::part2.runTests(day, listOf(
        "test" to 11387,
        "input" to 661823605105500,
    ))
}

private fun part1(lines: List<String>): Long {
    return readEquations(lines).mapNotNull {
        solve(it)
    }.sumOf { it.equation.goal }
}

private fun part2(lines: List<String>): Long {
    return readEquations(lines).mapNotNull {
        solve(it, true)
    }.sumOf { it.equation.goal }
}

private fun readEquations(lines: List<String>): List<Equation> {
    return lines.map {
        val (result, values) = it.split(":")
        Equation(
            goal = result.toLong(),
            values = values.splitToLongs()
        )
    }
}

private data class Equation(
    val goal: Long,
    val values: List<Long>
)

private enum class Operator(val opStr: String) {
    ADD("+"),
    MULTIPLY("*"),
    CONCAT("||")
}

private fun Solution.print() {
    print("${equation.goal} = ")
    equation.values.zip(ops).forEach {
        print("${it.first} ${it.second.opStr} ")
    }
    println("${equation.values[equation.values.lastIndex]}")
}

private data class Solution(
    val equation: Equation,
    val total: Long,
    val ops: List<Operator>
)

private fun solve(e: Equation, allowConcat: Boolean = false, total: Long = -1L, ops: List<Operator>? = null, depth: Int = 1): Solution? {
    if (total == e.goal && depth == e.values.size ) {
        return Solution(e, total, ops!!)
    }

    if (total > e.goal || depth > e.values.lastIndex) {
        return null
    }

    var cur = total
    if (total == -1L) {
        assert(depth == 1)
        cur = e.values[0]
    }

    assert(cur != -1L)

    val rhs = e.values[depth]

    val addOps = ops?.toMutableList()?.apply { add(Operator.ADD) } ?: listOf(Operator.ADD)
    assert(addOps.isNotEmpty())
    val addSolution = solve(e, allowConcat,cur + rhs, addOps, depth + 1)
    if (addSolution != null) {
        return addSolution
    }

    val mulOps = ops?.toMutableList()?.apply { add(Operator.MULTIPLY) } ?: listOf(Operator.MULTIPLY)
    assert(mulOps.isNotEmpty())
    val mulSolution = solve(e, allowConcat, cur * rhs, mulOps, depth + 1)
    if (mulSolution != null) {
        return mulSolution
    }

    if (allowConcat) {
        val catOps = ops?.toMutableList()?.apply { add(Operator.CONCAT) } ?: listOf(Operator.CONCAT)
        assert(mulOps.isNotEmpty())
        val catSolution = solve(e, true, (cur.toString() + rhs.toString()).toLong(), catOps, depth + 1)
        if (catSolution != null) {
            return catSolution
        }
    }

    return null
}