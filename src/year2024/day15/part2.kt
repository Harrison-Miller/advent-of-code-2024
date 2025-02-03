package day15

import utils.*
import utils.grid.*
import utils.vec2.*
import kotlin.math.min

fun main() {
    val day = packageName{}

    ::part2.runTests(day, listOf(
        "test2" to 618,
        "test3" to 9021,
        "input" to 1472235,
    ))
}

private fun part2(lines: List<String>): Long {
    val input = readPart2Input(lines)
    var state = input.state
    state.render(input.walls)

    input.moves.forEach { m ->
        state = movePart2(m, state, input.walls)
    }

    state.render(input.walls)

    return state.boxes.cells.filter{ !it.value.second }.map { (k, v) ->
        k.y*100L + k.x
    }.sumOf { it }
}

private fun movePart2(m: Char, s: State, w: SparseGrid<Char>): State {
    check(m in "<>^v")
    val nextPos = s.player.move(m)
    val cell = w.getOrDefault(nextPos, '.')
    val box = s.boxes.cells.get(nextPos)

    return if (box != null) {
        pushBoxes(m, s, w)
    } else {
        if (cell == '.') s.copy(player = nextPos) else s
    }
}

private fun pushBoxes(m: Char, s: State, w: SparseGrid<Char>): State {
    val boxesToPush = if (m in "<>") {
        findHorizontalBoxesUntilWallOrEmpty(m, s.player, s.boxes, w)
    } else {
        findVerticalBoxesUntilWallOrEmpty(m, s.player, s.boxes, w)
    }

    if (boxesToPush != null) {
        val nb = s.boxes.cells.mapKeys { (k, v) ->
            if (boxesToPush.contains(v.first)) {
                k.move(m)
            } else {
                k
            }
        }

        return s.copy( player = s.player.move(m), boxes = s.boxes.copy( cells = nb ))
    }

    return s
}

private fun findHorizontalBoxesUntilWallOrEmpty(m: Char, p: Vec2, b: BoxGrid, w: SparseGrid<Char>, boxesToPush: Set<Int> = emptySet()): Set<Int>? {
    check(m in "<>")
    val nextPos = p.move(m)
    val cell = w.getOrDefault(nextPos, '.')
    val box = b.cells.get(nextPos)

    if (cell == '#') {
        return null
    }

    if (box != null) {
        return findHorizontalBoxesUntilWallOrEmpty(m, nextPos, b, w, boxesToPush + box.first)
    }

    if (cell == '.') {
        return boxesToPush
    }

    return null
}

private fun findVerticalBoxesUntilWallOrEmpty(m: Char, p: Vec2, b: BoxGrid, w: SparseGrid<Char>, boxesToPush: Set<Int> = emptySet()): Set<Int>? {
    check(m in "^v")
    val nextPos = p.move(m)
    val cell = w.getOrDefault(nextPos, '.')
    val box = b.cells.get(nextPos)

    if (cell == '#') {
        return null
    }

    if (box != null) {
        // if isRight is true
        val firstHalfBoxes = findVerticalBoxesUntilWallOrEmpty(m, nextPos, b, w, boxesToPush + box.first) ?: return null


        val otherHalfPos = if(box.second) nextPos.left() else nextPos.right()
        val secondHalfBoxes = findVerticalBoxesUntilWallOrEmpty(m, otherHalfPos, b, w, boxesToPush + box.first) ?: return null

        return firstHalfBoxes + secondHalfBoxes
    }

    if (cell == '.') {
        return boxesToPush
    }

    return null
}

typealias BoxGrid = SparseGrid<Pair<Int, Boolean>>

private data class State(
    val player: Vec2,
    val boxes: BoxGrid, // box id, isRight
)

private data class Part2Input(
    val state: State,
    val walls: SparseGrid<Char>,
    val moves: String,
)

private fun readPart2Input(lines: List<String>): Part2Input {
    val (map, moves) = lines.partition { it.contains("#") }
    val wideMap = map.map {
        it.replace(".", "..").replace("#", "##").replace("O", "[]").replace("@", "@.")
    }
    val walls = wideMap.mapSparseGrid { c: Char ->
        when (c) {
            '#', '@' -> c
            else -> null
        }
    }

    var nextBoxID = 0
    val boxes = wideMap.mapSparseGrid { c: Char ->
        if (c == '[') ++nextBoxID to false else null
    }
    val boxCells = boxes.cells.toMutableMap()
    boxes.cells.forEach { it ->
        boxCells[it.key.right()] = it.value.first to true
    }

    val player = walls.findPlayer()
    return Part2Input(
        State(
            player = player,
            boxes = boxes.copy( cells = boxCells, width = walls.width, height = walls.height),
        ),
        walls = walls.copy( cells = walls.cells.toMutableMap().apply { remove(player) }),
        moves = moves.filter { it.isNotEmpty() }.joinToString(""),
    )
}

private fun State.render(walls: SparseGrid<Char>) {
    walls.render { c, p ->
        val b = boxes.cells[p]
        when {
            p == player -> '@'
            b != null -> if (b.second) ']' else '['
            c != null -> c
            else -> '.'
        }
    }
}