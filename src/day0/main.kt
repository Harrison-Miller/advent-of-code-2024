package day0

import utils.*

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 6,
        "input" to 45,
    ))

    ::part2.runTests(day, listOf(
        "test" to 6,
        "input" to 362880,
    ))
}

private fun part1(input: List<String>): Int {
    return input.toInts().sum()
}

private fun part2(input: List<String>): Int {
    var total: Int = 0
    input.toInts().forEach {
        if (total == 0) {
            total = it
        } else {
            total *= it
        }
    }
    return total
}