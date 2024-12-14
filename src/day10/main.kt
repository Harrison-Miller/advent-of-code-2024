package day10

import utils.*
import utils.grid.filter
import utils.grid.mapGrid
import utils.grid.mapSparseGrid

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "simple" to 1,
        "test" to 36,
        "input" to 796,
    ))

    ::part2.runTests(day, listOf(
        "simple" to 16,
        "test" to 81,
        "input" to 1942,
    ))
}

private fun part1(lines: List<String>): Int {
//    val grid = lines.mapGrid { it.toString().toInt() }
//    val peaks = grid.filter { it == 0 }

    return 0
}

private fun part2(lines: List<String>): Int {
    return 0
}