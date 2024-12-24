package day21

import utils.*
import utils.vec2.*
import kotlin.math.abs

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 0,
//        "input" to null,
    ))

//    ::part2.runTests(day, listOf(
//        "test" to 0,
//        "input" to null,
//    ))
}

private fun part1(lines: List<String>): Int {
    return 0
}

private fun part2(lines: List<String>): Int {
    return 0
}

private fun generateAllNumPadMoves(): Map<Char, Map<Char, Move>> {
    return emptyMap()
}

private fun generateAllDirPadMoves(): Map<String, List<Move>> {
    // generate all key -> key moves
    // then generate all moves subsequent moves
    return emptyMap()
}

// A move represents a number of key presses on the direction pad
// It is a pair of Vec2, Vec2 because the order of key presses matter
// So v<< would be (0,1) (-2,0), but <<v (-2,0) (0,1)
// Each Vec2 should only ever have one component used, the other should always be 0
typealias Move = Pair<Vec2, Vec2>

// ordered returns the Move reordered to make it as quick as possible for higher depths
// rules are < before ^/v before >, except when going across the gap
// so ^^^<< would be reordered to <<^^^ unless this causes the move to cross the gap.
// for example: start (2, 3), gap: (0, 3) Move = (-2,0) (0,-3). Is the correct ordering until we consider the gap, which forces it to be reordered.
// for consistency, (0, 0) will always come last
private fun Move.ordered(start: Vec2, gap: Vec2): Move {
    // (0,0) should always be last
    if (first == Directions.ZERO) {
        return swapped()
    }

    // if the second part of the move is 0,0 no reordering can be done
    if (second == Directions.ZERO) {
        return this
    }

    val delta = first + second
    val end = start + delta

    val yfirst = Move(Vec2(0, delta.y), Vec2(delta.x, 0))
    val xfirst = Move(Vec2(delta.x, 0), Vec2(0, delta.y))

    // x must be first to avoid the gap
    if (start.x == gap.x && end.y == gap.y) {
        return xfirst
    }

    // y must be first to avoid the gap
    if (start.y == gap.y && end.x == gap.x) {
        return yfirst
    }

    // < before ^/v before >
    return if (abs(delta.y) > 0 && delta.x >= 0) yfirst else xfirst
}

private fun Vec2.toMove(start: Vec2, gap: Vec2) = Move(Vec2(x, 0), Vec2(0, y)).ordered(start, gap)