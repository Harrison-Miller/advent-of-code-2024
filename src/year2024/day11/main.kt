package day11

import utils.*

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 55312,
        "input" to 182081,
    ))

    ::part2.runTests(day, listOf(
        "input" to 216318908621637,
    ))
}

private fun part1(lines: List<String>): Long {
    var stones = lines.single().split(" ").map { it.toLong() to 1L }.toMap()
    (1..25).forEach {
       stones = blink(stones)
    }
    return stones.map { it.value }.sumOf { it }
}

private fun part2(lines: List<String>): Long {
    var stones = lines.single().split(" ").map { it.toLong() to 1L }.toMap()
    (1..75).forEach {
        stones = blink(stones)
    }
    return stones.map { it.value }.sumOf { it }
}

private fun blink(stones: Map<Long, Long>): Map<Long, Long> {
    return stones.map { (stone, count) ->
        when {
            stone == 0L -> listOf(1L to count)
            stone.toString().length%2 == 0 -> {
                val s = stone.toString()
                listOf (
                    s.substring(0, s.length/2).toLong() to count,
                    s.substring(s.length/2, s.length).toLong() to count
                )
            }
            else -> listOf(stone*2024L to count)
        }
    }.flatten().groupByPair().mapValues {
        it.value.sum()
    }
}