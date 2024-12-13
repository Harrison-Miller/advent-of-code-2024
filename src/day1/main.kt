package day1

import utils.*
import kotlin.math.abs

fun main() {
    val day = "day1"
    ::part1.runTests(day, listOf(
        "test" to 11,
        "input" to 2815556,
    ))

    ::part2.runTests(day, listOf(
        "test" to 31,
        "input" to 23927637,
    ))
}

private fun part1(input: List<String>): Int {
    val (left, right) = input.map {
        val (left, right) = it.splitToInt()
        left to right
    }.unzip()

    return left.sorted().zip(right.sorted()).map { (a, b) ->
        abs(a - b)
    }.sumOf { it }
}

private fun part2(input: List<String>): Int {
    val (left, right) = input.map {
        val (left, right) = it.splitToInt()
        left to right
    }.unzip()

    val counted = right.countBy { it }
    return left.sumOf {
        it * counted.getOrDefault(it, 0)
    }
}