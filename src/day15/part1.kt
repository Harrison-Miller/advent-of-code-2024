package day15

import utils.*
import utils.grid.*
import utils.vec2.*

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to null,
        "input" to null,
    ))
}

private fun part1(lines: List<String>): Long {
    var (player, grid, moves) = readPart1Input(lines)
    grid.render(player)

    moves.forEach { m ->
        val step = movePart1(m, player, grid)
        player = step.first
        grid = step.second

    }

    grid.render(player)
    return grid.cells.map { (k, v) ->
        if (v == 'O') k.x*1L + k.y*100L else 0
    }.sumOf { it }
}

private data class Par1Input(
    val player: Vec2,
    val grid: SparseGrid<Char>,
    val moves: String,
)

internal fun Vec2.move(m: Char): Vec2 {
    return when (m) {
        '<' -> left()
        '>' -> right()
        '^' -> up()
        'v' -> down()
        else -> this
    }
}


private fun movePart1(m: Char, p: Vec2, g: SparseGrid<Char>): Pair<Vec2, SparseGrid<Char>> {
    check(m in "<>^v")
    val nextPos = p.move(m)
    val cell = g.getOrDefault(nextPos, '.')
    return when (cell) {
        '.' -> nextPos to g
        'O' -> {
            val nextCell = findNextEmptyOrWallPart1(m, nextPos, g)
            when (nextCell.second) {
                '.' -> {
                    val ng = g.copy(cells = g.cells.toMutableMap().apply {
                        set(nextCell.first, 'O')
                        remove(nextPos)
                    })
                    nextPos to ng
                }
                else -> p to g
            }
        }
        else -> p to g
    }
}

private fun findNextEmptyOrWallPart1(m: Char, p: Vec2, g: SparseGrid<Char>): Pair<Vec2, Char> {
    val nextPos = p.move(m)
    val cell = g.getOrDefault(nextPos, '.')
    return when (cell) {
        '.', '#' -> nextPos to cell
        'O' -> findNextEmptyOrWallPart1(m, nextPos, g)
        else -> p to cell
    }
}

private fun readPart1Input(lines: List<String>): Par1Input {
    val (map, moves) = lines.partition { it.contains("#") }
    val grid = map.mapSparseGrid { c: Char ->
        when (c) {
            '#', 'O', '@' -> c
            else -> null
        }
    }
    val player = grid.findPlayer()
    return Par1Input(
        player = player,
        grid = grid.copy( cells = grid.cells.toMutableMap().apply { remove(player) }),
        moves = moves.filter { it.isNotEmpty() }.joinToString(""),
    )
}

internal fun SparseGrid<Char>.findPlayer(): Vec2 = cells.filter { it.value == '@'}.toList().single().first

private fun SparseGrid<Char>.render(player: Vec2) = render { c, pos ->
    when {
        player == pos -> '@'
        c != null -> c
        else -> null
    }
}