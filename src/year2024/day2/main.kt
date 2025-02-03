package day2

import utils.*

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 2,
        "input" to 306,
    ))

    ::part2.runTests(day, listOf(
        "test" to 4,
        "input" to 366,
    ))
}

private fun part1(lines: List<String>): Int {
    return lines.count {
        it.splitToInts().isSafe()
    }
}

private fun part2(lines: List<String>): Int {
    return lines.count {
        it.splitToInts().combinations().any { it.isSafe() }
    }
}

private fun List<Int>.isSafe(): Boolean {
    val diffs = zipWithNext { a, b -> a - b}
    return diffs.all { it > -4 && it < 0  } || diffs.all { it > 0 && it < 4}
}

private fun List<Int>.combinations(): List<List<Int>> {
    return mapIndexed { index, _ ->
        slice(0..index-1) + slice(index+1..size-1)
    }
}