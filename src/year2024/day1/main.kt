package year2024.day1

import utils.*
import kotlin.math.abs

fun main() {
    val day = packageName{}

    ::part1.runTests(day, listOf(
        "test" to 11,
        "input" to 2815556,
    ))

    ::part2.runTests(day, listOf(
        "test" to 31,
        "input" to 23927637,
    ))
}

private fun part1(lines: List<String>): Int {
    val (left, right) = lines.map {
        val (left, right) = it.splitToInts()
        left to right
    }.unzip()

    return left.sorted().zip(right.sorted()).map { (a, b) ->
        abs(a - b)
    }.sumOf { it }
}

private fun part2(lines: List<String>): Int {
    val (left, right) = lines.map {
        val (left, right) = it.splitToInts()
        left to right
    }.unzip()

    val counted = right.countBy { it }
    return left.sumOf {
        it * counted.getOrDefault(it, 0)
    }
}
