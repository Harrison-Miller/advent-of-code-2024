package day18

import utils.*
import utils.graph.adjacencyMatrix
import utils.graph.dijkstra
import utils.grid.render
import utils.vec2.*

fun main() {
    val day = packageName{}
//    ::part1.runTests(day, listOf(
//        "input" to null,
//    ))

    ::part2.runTests(day, listOf(
        "input" to null,
    ))
}

private fun readInput(lines: List<String>): Pair<Set<Vec2>, List<Vec2>> {
    val grid = (0L..70L).map { y ->
        (0L..70L).map { x ->
            Vec2(x, y)
        }
    }.flatten().toSet()

    val blocks = lines.map {
        it.toVec2()
    }

    return grid to blocks
}

private fun part1(lines: List<String>): Long {
    val (grid, blocks) = readInput(lines)
    (grid - blocks.take(1024).toSet()).render(71, 71) { open, pos ->
        when {
            pos == Vec2(0, 0) -> 'S'
            pos == Vec2(70, 70) -> 'E'
            open -> '.'
            else -> '#'
        }
    }
    val graph = grid.adjacencyMatrix { _, _ -> 1L }
    val paths = dijkstra(graph, Vec2(0, 0))
    return paths[Vec2(70, 70)] ?: 0
}

private fun part2(lines: List<String>): String {
    var (grid, blocks) = readInput(lines)
    grid = grid - blocks.take(2500).toSet() // rough manual search the answer is between block 2500 and 3000
    val nextBlockIndex = 2501

    (nextBlockIndex..3000).forEach {
        val nextBlock = blocks[it]
        grid = grid - nextBlock
        val graph = grid.adjacencyMatrix { _, _ -> 1L }
        val paths = dijkstra(graph, Vec2(0, 0))
        val dist = paths[Vec2(70, 70)] ?: -1
        if (dist == -1L) {
            return "${nextBlock.first},${nextBlock.second}"
        }
    }
    return ""
}