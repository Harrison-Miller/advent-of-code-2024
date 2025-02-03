package day14

import utils.*
import utils.geometry.Square
import utils.geometry.inside
import utils.vec2.*
import utils.grid.*

fun main() {
    val day = packageName{}
//    ::part1.runTests(day, listOf(
//        "test" to 12,
//        "input" to 224969976,
//    ))

    ::part2.runTests(day, listOf(
        "input" to 7892,
    ))
}

fun readRobots(lines: List<String>): SparseGrid<List<Vec2>> = lines.mapSparseGrid { l: String ->
    val (p, v) = l.split(" ")
    p.substringAfter("=").toVec2() to listOf(v.substringAfter("=").toVec2())
}

private fun part1(lines: List<String>): Long {
    val robots = readRobots(lines)
    val newRobots = robots.step(100L)
    return countQuadrants(newRobots)
}

private fun countQuadrants(robots: SparseGrid<List<Vec2>>): Long {
    val topLeft = robots.cells.filter { it.key.x < robots.width/2 && it.key.y < robots.height/2 }.sumRobots()
    val topRight = robots.cells.filter { it.key.x > robots.width/2 && it.key.y < robots.height/2 }.sumRobots()


    val botLeft = robots.cells.filter { it.key.x < robots.width/2 && it.key.y > robots.height/2 }.sumRobots()
    val botRight = robots.cells.filter { it.key.x > robots.width/2 && it.key.y > robots.height/2 }.sumRobots()

    return topLeft * topRight * botLeft * botRight
}

private fun Map<Vec2, List<Vec2>>.sumRobots(): Long {
    return map {
        it.value.size
    }.sumOf { it }.toLong()
}

private fun part2(lines: List<String>): Int {
    var robots = readRobots(lines)
    val densityMap = generateDensityMap(robots.width, robots.height)

    val best = (1..10000).map{ i ->
        robots = robots.step()
        val d = robots.cells.getDensity(densityMap)
        i to d to robots
    }.maxBy { it.first.second }

    best.second.render()
    val step = best.first.first

    return step
}

private fun SparseGrid<List<Vec2>>.step(steps: Long = 1): SparseGrid<List<Vec2>> {
    return copy( cells = cells.map { (p, vs) ->
        vs.map {
            (p+it*steps).wrap(dimensions()) to it
        }
    }.flatten().groupByPair())
}

private fun SparseGrid<List<Vec2>>.render() = cells.render(width, height)

private fun Map<Vec2, List<Vec2>>.render(width: Int, height: Int) = render(width, height) { l, _ ->
    l?.size?.toString()?.single() ?: '.'
}

fun generateDensityMap(width: Int, height: Int): List<List<Int>> {
    val shape = Square(
        Pair(width/4, height/4).toVec2(),
        (width/2).toLong(),
        (height/2).toLong(),
    )

    return (0..height).map { y ->
        (0..width).map { x ->
            if (shape.inside(Pair(x, y).toVec2())) 1 else 0
        }
    }
}

fun Map<Vec2, List<Vec2>>.getDensity(densityMap: List<List<Int>>): Long {
    return densityMap.mapIndexed { y, row ->
        row.mapIndexed { x, d ->
            if (d != 0) {
                val v = getOrDefault(Pair(x, y).toVec2(),  emptyList())
                d * v.size
            } else {
                0
            }
        }
    }.flatten().sumOf { it }.toLong()
}
