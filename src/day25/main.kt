package day25

import utils.*

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 3,
        "input" to null,
    ))

//    ::part2.runTests(day, listOf(
//        "test" to 0,
//        "input" to null,
//    ))
}

private fun part1(lines: List<String>): Int {
    val input = readInput(lines)
    input.println()

    return input.keys.sumOf { key ->
        input.locks.count { lock ->
            fit(key, lock)
        }
    }
}

private fun fit(key: List<Int>, lock: List<Int>): Boolean {
    check(key.size == lock.size) { "key and lock are different sizes" }
    return key.mapIndexed { i, p -> p+lock.get(i) }.all { it < 6 }
}

private fun part2(lines: List<String>): Int {
    return 0
}

private data class Input(
    val locks: List<List<Int>>,
    val keys: List<List<Int>>,
)

private fun readInput(lines: List<String>): Input {
    val schematics = lines.chunked(8)
    return Input(
        locks = schematics.filter { it.first().first() == '#' }.map(::readHeightMap),
        keys = schematics.filter { it.first().first() == '.' }.map(::readHeightMap),
    )
}

private fun readHeightMap(lines: List<String>): List<Int> {
    return lines.map { row ->
        row.mapIndexedNotNull { i, c ->
            if (c == '#') i to c else null
        }
    }.flatten().groupByPair().mapValues { it.value.size - 1 }.toList().sortedBy { it.first }.map { it.second }
}
