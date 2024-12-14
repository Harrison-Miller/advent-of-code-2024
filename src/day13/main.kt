package day13

import utils.*

val debug = true

fun main() {
    val day = packageName{}
//    ::part1.runTests(day, listOf(
//        "simple" to 2,
//        "first" to 280,
//        "test" to 480,
//        "input" to 29877,
//    ))

    ::part2.runTests(day, listOf(
        "simple" to 50000000000000L,
//        "test" to null,
//        "input" to null,
    ))
}

private fun part1(lines: List<String>): Long {
    val input = lines.toInput()
    return input.clawMachines.sumOf {
        val graph = it.toGraph()
        if (graph.nodes.contains(it.prize)) {
            elapsedTime("dijkstra for ${it.prize}", debug) {
                dijkstra(graph.adjacencyMatrix, Vec2(0, 0)).getOrDefault(it.prize, 0)
            }
        } else {
            0
        }
    }
}

private fun part2(lines: List<String>): Long {
    val input = lines.toInput()
    return input.clawMachines.map {
        it.copy(prize = it.prize + Vec2(10000000000000L, 10000000000000L))
    }.sumOf {
        0L // TODO: create and solve a system of equations. Filter by whole numbers. map to cost. min of.
    }
}


internal fun ClawMachine.toGraph(): Graph<Vec2> {
    val points = elapsedTime("points", debug) {
        setOf(Vec2(0, 0)).floodFill {
            listOf(
                it + a,
                it + b
            ).filterNot { it.x > prize.x || it.y > prize.y }.toSet()
        }
    }

    val edges = elapsedTime("adjacencyMatrix", debug) {
        points.mapNotNull { from ->
             listOfNotNull(
                if (points.contains(from+b)) from to (from+b to 1L) else null,
                if (points.contains(from+a)) from to (from+a to 3L) else null
             ).ifEmpty { null }
        }.flatten().groupByPair()
    }

    return Graph(points, edges)
}


internal data class ClawMachine(
    val a: Vec2, // costs 3
    val b: Vec2, // costs 1
    val prize: Vec2
)

private fun List<String>.toClawMachine(): ClawMachine {
    check(size == 3) {
        "toClawMachine received to many lines: $this"
    }

    val (a, b, prize) = this

    return ClawMachine(
        a=Vec2(
            a.substringAfter("Button A: X+").substringBefore(",").toLong(),
            a.substringAfter(", Y+").toLong()
        ),
        b=Vec2(
            b.substringAfter("Button B: X+").substringBefore(",").toLong(),
            b.substringAfter(", Y+").toLong()
        ),
        prize=Vec2(
            prize.substringAfter("Prize: X=").substringBefore(",").toLong(),
            prize.substringAfter(", Y=").toLong()
        ),
    )
}

private data class Input(
    val clawMachines: List<ClawMachine>
)

private fun List<String>.toInput(): Input {
    return Input(
        clawMachines = chunked(4) { if(it.size > 3) it.dropLast(1) else it }.map {
            it.toClawMachine()
        }
    )
}