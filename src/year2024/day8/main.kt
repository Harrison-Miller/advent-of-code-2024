package day8

import utils.*
import utils.grid.filterInBounds
import utils.grid.render
import utils.vec2.*

var debug = false

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 14,
        "input" to 276,
    ))

    ::part2.runTests(day, listOf(
        "simple" to 9,
        "test" to 34,
        "input" to 991,
    ))
}

private fun part1(lines: List<String>): Int {
    val input = readInput(lines)
    val antinodes = input.antennas.map {
        getAntinodes(it.value)
    }.flatten().filterInBounds(input.width, input.height).toSet()

    debug.doif {
        input.antennas.flipFlatten().render(input.width, input.height) { cell, pos ->
            when {
                cell != null -> cell
                antinodes.contains(pos) -> '#'
                else -> null
            }
        }
    }

    return antinodes.size
}

private fun part2(lines: List<String>): Int {
    val input = readInput(lines)
    val antinodes = input.antennas.map {
        getAntinodes(it.value, (1..100))
    }.flatten().filterInBounds(input.width, input.height).toSet()

    debug.doif {
        input.antennas.flipFlatten().render(input.width, input.height) { cell, pos ->
            when {
                cell != null -> cell
                antinodes.contains(pos) -> '#'
                else -> null
            }
        }
    }

    return antinodes.size
}

data class Input(
    val width: Int,
    val height: Int,
    val antennas: Map<Char, List<Vec2>>
)

fun readInput(lines: List<String>): Input {
    return Input(
        width = lines[0].length,
        height = lines.size,
        antennas = lines.mapIndexed { y, l ->
            l.mapIndexedNotNull { x, c ->
                if (c.isLetterOrDigit()) Pair(c, Vec2(x.toLong(), y.toLong())) else null
            }
        }.flatten().groupBy(
            keySelector = { it.first },
            valueTransform = { it.second },
        )
    )
}

fun getAntinodes(antennas: List<Vec2>, range: IntRange = 2..2): Set<Vec2> {
    val nodes = antennas.map { a ->
        antennas.filter { a != it }.map { b ->
            listOf(
                range.map{b + ((a - b) * it)},
                range.map{a + ((b - a) * it)}
            ).flatten()
        }.flatten()
    }.flatten().toSet()
    return nodes
}