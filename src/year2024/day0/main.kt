package year2024.day0

import utils.*

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 0,
//        "input" to null,
    ))

//    ::part2.runTests(day, listOf(
//        "test" to 0,
//        "input" to null,
//    ))
}

private fun part1(lines: List<String>): Int {
    return lines.first().toInt()
}

private fun part2(lines: List<String>): Int {
    return 0
}
