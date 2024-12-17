package day16

import utils.*
import utils.graph.*
import utils.grid.*
import utils.vec2.*

/*
Use dijkstras to find the shortest path through the maze.
The weight between all nodes is 1 except corners and intersections
A corner has 2 adjacent nodes that are perpendicular to each other (x1 != x2 && y1 != y2)
A corner can have weight 1 entering it but weight 1000 exiting it.

A T intersection has 3 adjacent nodes. 2 nodes will share a coordinate. Those 2 nodes will be along the line of travel.
Entering an exiting the T intersection along the line of travel will be weight 1. However, entering and exiting from the side will be weight 1000.
That is if you enter from the side you must turn either left or right. However, if you enter from the direction of travel you can travel straight, or turn.

A 4-way intersection has 2 directions of travel so doing the above doesn't work. Instead, we pick one direction of travel.
For convenience, we'll always pick the vertical and consider it the primary direction of the travel. We'll treat it like a T intersection.
Going into and out of the intersection in the primary direction is weight 1. Going into and out of the intersection in the secondary direction is weight 1000.
Then we add another set of edges connecting the two secondary direction nodes with weight 2.

Separately we need to weight the starting node correctly.
So that east is 1, south and north is 1000 and west is 2000.

Note: The act of turning 90 degrees not moving onto the tile at 90 degrees cost 1000. So if youn want to move on to the tile at 90 degrees you must first (turn 1000, then step 1).
So all of the above weights should be 1001 and 2001 where applicable.
 */

fun main() {
    val day = packageName{}
//    ::part1.runTests(day, listOf(
//        "test" to 7036,
//        "input" to 135536,
//    ))

    ::part2.runTests(day, listOf(
        "test" to 45,
        "test2" to 64,
        "input" to 583,
    ))
}

private fun part1(lines: List<String>): Long {
    val input = readInput(lines)

    val (graph, _) = input.buildEdges()

    val distances = dijkstra(graph, input.start)

    return distances.getOrDefault(input.end, Long.MAX_VALUE)
}

private fun part2(lines: List<String>): Int {
    val input = readInput(lines)

    val (graph, fourWayIntersections) = input.buildEdges()

    val paths = dijkstraWithAllPaths(graph, input.start)
    var pathsToGoal = paths.getPaths(input.end) + input.end

    // The weights skip over 4-way intersection when moving horizontal
    // so we have to read them to the path
    pathsToGoal += fourWayIntersections.mapNotNull {
        if (pathsToGoal.contains(it.left()) && pathsToGoal.contains(it.right())) it else null
    }.toSet()
    input.render(pathsToGoal)

    return pathsToGoal.size
}

private fun getIntersectionInfo(nodes: Set<Vec2>, a: Vec2): Triple<Boolean, Boolean, Boolean> {
    val left = nodes.contains(a.left())
    val right = nodes.contains(a.right())
    val up = nodes.contains(a.up())
    val down = nodes.contains(a.down())

    val ns = up && down
    val ew = left && right

    val corner = ((left && up) || (left && down) || (right && up) || (right && down)) && !ew && !ns


    val tIntersection = (ns && (left xor right)) || (ew && (up xor down))


    val fourWay = ns && ew

    check(!(corner && (tIntersection || fourWay)) && !(tIntersection && fourWay)) {
        "a node can only be one type of intersection (corner, T, 4-way)"
    }

    return Triple(corner, tIntersection, fourWay)
}

private fun Input.buildEdges(): Pair<AdjacencyMatrix<Vec2>, Set<Vec2>> {
    val fourWayIntersections = mutableSetOf<Vec2>()
    val graph = nodes.adjacencyMatrix { a, b ->
        val (corner, tIntersection, fourWay) = getIntersectionInfo(nodes, a)
        if (fourWay) {
            fourWayIntersections.add(a)
        }

        val (_, bTIntersection, bFourWay) = getIntersectionInfo(nodes, b)
        when {
            // handle turning at the start position
            a == start -> when (b) {
                a.up(), a.down() -> 1001
                a.left() -> 2001
                else -> 1
            }
            // handle leaving a corner (always a turn)
            corner -> 1001
            // This handles leaving the tIntersection to the side node
            // a node is the intersection
            tIntersection -> getTIntersectionWeight(a, b, nodes)
            // This handles entering a t intersection from the side node
            // b node is the intersection
            bTIntersection -> getTIntersectionWeight(b, a, nodes)
            // handles leaving a 4-way intersection
            // a node is the intersection
            fourWay -> get4WayIntersectionWeight(a, b, nodes)
            // handles entering a 4-way intersection
            // b node is the intersection
            bFourWay -> get4WayIntersectionWeight(b, a, nodes)
            else -> 1
        }

    }

    // handle the special connection for horizontal movement across a 4 way intersection
    val ng = graph.toMutableMap()
    fourWayIntersections.forEach { intersection ->
        val a = intersection.left()
        val b = intersection.right()

        val ae = ng.getOrDefault(a, emptyList())
        ng[a] = ae + (b to 2)

        val be = ng.getOrDefault(b, emptyList())
        ng[b] = be + (a to 2)
    }

    return ng to fourWayIntersections
}

private fun getTIntersectionWeight(intersection: Vec2, other: Vec2, nodes: Set<Vec2>): Long {
    val ns = nodes.contains(intersection.up()) && nodes.contains(intersection.down())
    val ew = nodes.contains(intersection.left()) && nodes.contains(intersection.right())
    return when {
        ns && (other == intersection.left() || other == intersection.right()) -> 1001
        ew && (other == intersection.up() || other == intersection.down()) -> 1001
        else -> 1
    }
}

private fun get4WayIntersectionWeight(intersection: Vec2, other: Vec2, nodes: Set<Vec2>): Long {
    return when (other) {
        intersection.left(), intersection.right() -> 1001
        else -> 1
    }
}

private data class Input(
    val width: Int,
    val height: Int,
    val start: Vec2,
    val end: Vec2,
    val nodes: Set<Vec2>
)

private fun readInput(lines: List<String>): Input {
    var start = Vec2(0, 0)
    var end = Vec2(0, 0)
    val nodes = lines.mapIndexed { y, row ->
        row.mapIndexedNotNull{ x, c ->
            val v = Pair(x, y).toVec2()
            when (c) {
                'S' -> {
                    start = v
                    v
                }
                'E' -> {
                    end = v
                    v
                }
                '.' -> v
                else -> null
            }
        }
    }.flatten().toSet()

    return Input(
        width = lines[0].length,
        height = lines.size,
        start = start,
        end = end,
        nodes = nodes,
    )
}

private fun Input.render(pathToGoal: Set<Vec2>? = null) = nodes.render(width, height) { n, v ->
    when {
        pathToGoal?.contains(v) ?: false -> 'O'
        v == start -> 'S'
        v == end -> 'E'
        n -> '.'
        else -> '#'
    }
}