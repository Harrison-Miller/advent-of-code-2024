package day3

import utils.*

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 161,
        "input" to 187833789,
    ))

    ::part2.runTests(day, listOf(
        "test2" to 48,
        "input" to 94455185,
    ))
}

private fun part1(input: List<String>): Long {
    return mulAndSum(input.joinToString(""))
}

private fun part2(input: List<String>): Long {
    return input.joinToString("").split("do()").sumOf {
        mulAndSum(it.split("don't()")[0])
    }
}

private val mul = "mul\\(([0-9]{1,3}),([0-9]{1,3})\\)".toRegex()

private fun mulAndSum(s: String): Long {
    return mul.findAll(s).sumOf { it ->
        val (a, b) = it.destructured
        a.toLong() * b.toLong()
    }
}