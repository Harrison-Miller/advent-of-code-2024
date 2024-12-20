package day20

import utils.*
import utils.graph.*
import utils.vec2.*

fun main() {
    val day = packageName{}
//    ::part1.runTests(day, listOf(
//        "test" to 5,
//        "input" to 1346
//    ))

    ::part2.runTests(day, listOf(
        "input" to null,
    ))
}

data class Input(
    val width: Int,
    val height: Int,
    val start: Vec2,
    val end: Vec2,
    val nodes: Set<Vec2>
)

private fun part1(lines: List<String>): Int {
    val input = readInput(lines)

    val graph = input.nodes.adjacencyMatrix { _, _ -> 1L }
    val distanceToEnd = dijkstra(graph, input.end)

    val cheats = countCheatsWithThreshold(input, distanceToEnd, 100, 2)
    input.render(cheats)
    return cheats.count()
}

private fun part2(lines: List<String>): Int {
    val input = readInput(lines)

    val graph = input.nodes.adjacencyMatrix { _, _ -> 1L }
    val distanceToEnd = dijkstra(graph, input.end)

    val cheats = countCheatsWithThreshold(input, distanceToEnd, 100, 20)
    //input.render(cheats)
    return cheats.count()
}

// 1. reverse dijkstra (get distance from any point to end)
// 2. get distance from start -> end
// part 1: for all spaces 2 away is the distance saved >= thresholds

private fun countCheatsWithThreshold(input: Input, distanceToEnd: Map<Vec2, Long>, threshold: Long, maxCheatDistance: Int): Set<Pair<Vec2, Vec2>> {
    val baseLine = distanceToEnd[input.start]!!
    println("baseLine: $baseLine")
    val cheats = distanceToEnd.map{ (start, startDistance) ->
        val possibleCheats = distanceToEnd.filterKeys { it != start && start.cartesianDistTo(it) <= maxCheatDistance }
//        val possibleCheats = listOf(start.left().left(), start.right().right(), start.up().up(), start.down().down()).filter { distanceToEnd.contains(it) }
        possibleCheats.mapNotNull { (end, endDistance) ->
            // distance traveled so far + cheat distance + remaining distance to end
            val distanceTraveled = (baseLine - startDistance) + start.cartesianDistTo(end) + endDistance
            val distanceSaved = baseLine - distanceTraveled
            if (distanceSaved >= threshold) start+start.dirTo(end) to end else null
        }
    }.flatten().toSet()

    return cheats
}

private fun readInput(lines: List<String>): Input {
    var start = Vec2(0,0)
    var end = Vec2(0,0)
    val nodes = lines.mapIndexed { y, row ->
        row.mapIndexedNotNull { x, c ->
            val p = Pair(x, y).toVec2()
            when (c) {
                'S' -> {
                    start = p
                    p
                }
                'E' -> {
                    end = p
                    p
                }
                '.' -> p
                else -> null
            }
        }
    }.flatten()

    return Input(
        width = lines[0].length,
        height = lines.size,
        start = start,
        end = end,
        nodes = nodes.toSet(),
    )
}
private fun Input.render(cheats: Set<Pair<Vec2, Vec2>>) {
    (0..<height).forEach{  y ->
        (0..<width).forEach { x ->
            val p = Pair(x, y).toVec2()
            val c = when {
                cheats.any { it.first == p} -> "1"
                cheats.any { it.second == p} -> "2"
                p == start -> 'S'
                p == end -> 'E'
//                pathToGoal.contains(p) -> '^'
                nodes.contains(p) -> '.'
                else -> '#'
            }
            print(c)
        }
        println("")
    }
}
