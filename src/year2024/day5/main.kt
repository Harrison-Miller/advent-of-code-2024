package day5

import utils.*
import kotlin.math.ceil

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 143,
        "input" to 6612,
    ))

    ::part2.runTests(day, listOf(
        "test" to 123,
        "input" to 4944,
    ))
}

private fun part1(lines: List<String>): Int {
    val input = lines.toInput()
    val sortFunc = ruleSort(input.rules)
    return input.updates.filter {
        it == it.sortedWith(sortFunc)
    }.sumOf { it.middle() }
}

private fun part2(lines: List<String>): Int {
    val input = lines.toInput()
    val sortFunc = ruleSort(input.rules)
    return input.updates.filter {
        it != it.sortedWith(sortFunc)
    }.sumOf { it.sortedWith(sortFunc).middle() }
}

private data class Input(
    val rules: List<Pair<Int, Int>>,
    val updates: List<List<Int>>
)

private fun List<String>.toInput(): Input {
    val (rules, updates) = this.partition { it.contains("|") }
    return Input(
        rules = rules.map {
            val (a, b) = it.splitToInts(Regex("\\|"))
            a to b
        },
        updates = updates.filter{ it.isNotEmpty() }.map { it.splitToInts(Regex(",")) }
    )
}

private fun ruleSort(rules: List<Pair<Int, Int>>): (a: Int, b: Int) -> Int {
    return { a, b ->
        if (rules.indexOf(Pair(a, b)) >= 0) {
            -1
        } else if (rules.indexOf(Pair(b, a)) >= 0) {
            1
        } else {
            0
        }
    }
}

private fun List<Int>.middle(): Int {
    var m = ceil((this.size/2).toDouble()).toInt()
    return this[m]
}