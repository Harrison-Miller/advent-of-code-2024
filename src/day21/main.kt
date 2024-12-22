package day21

import utils.*
import utils.vec2.*
import kotlin.math.abs

/*
+---+---+---+
| 7 | 8 | 9 |
+---+---+---+
| 4 | 5 | 6 |
+---+---+---+
| 1 | 2 | 3 |
+---+---+---+
    | 0 | A |
    +---+---+
 */

/*
    +---+---+
    | ^ | A |
+---+---+---+
| < | v | > |
+---+---+---+
 */



fun main() {
    val day = packageName{}
//    ::part1.runTests(day, listOf(
//        "test" to 126384,
//        "input" to 176650,
//    ))

    ::part2.runTests(day, listOf(
        "input" to null,
    ))
}


private fun part1(lines: List<String>): Long {
    val dirPadMoves = generateDirPadSequenceMap()
    return lines.sumOf { code ->
        val x = code.trimStart('0').trimEnd('A').toLong()
        val moves = calcRobotMoves(dirPadMoves, code, 2, numPadMap['A']!!)
        x * moves
    }
}

// Guesses:
// 456937420748588 to high
private fun part2(lines: List<String>): Long {
    val dirPadMoves = generateDirPadSequenceMap()
    return lines.sumOf { code ->
        val x = code.trimStart('0').trimEnd('A').toLong()
        val moves = calcRobotMoves(dirPadMoves, code, 25, numPadMap['A']!!)
        x * moves
    }
}

private fun calcRobotMoves(dirPadMoves: Map<Vec2, List<Vec2>>, code: String, depth: Int = 2, s: Vec2 = Vec2(2, 3)): Long {
    var start = s
    var moves = code.map { c ->
        var end = numPadMap[c]!!
        // generate moves for first dir pad
        val moves = calcNextNumPadMoves(start, end)
        start = end
        moves
    }.flatten().groupBy { it }.mapValues { it.value.count().toLong() }
//    moves.println()
//    moves.formatted().println()

    (2..depth).forEach {
        moves = nextMoves(dirPadMoves, moves)
//        moves.println()
//        moves.formatted().println()
    }

    return moves.map { (m, c) ->
        (m.cartesianLength()+1) * c
    }.sum()
}

private fun calcNextNumPadMoves(numPadStart: Vec2, numPadEnd: Vec2): List<Vec2> {
    val delta = numPadEnd - numPadStart

    var dirPadStart = Vec2(2, 0)
    val forwardMoves = delta.toSteps{
        (numPadStart.y == 3L && numPadEnd.x == 0L) ||
                delta.y > 0 && !(numPadStart.x == 0L && numPadEnd.y == 3L)
    }.map { dir ->
        val c = dirToChar[dir]!!
        val end = dirPadMap[c]!!
        val d = (end - dirPadStart)
        dirPadStart = end
        d
    }

    val backwardsMove = (Vec2(2, 0) - dirPadStart)

    return forwardMoves + backwardsMove
}

private fun nextMoves(sequences: Map<Vec2, List<Vec2>>, moves: Map<Vec2, Long>): Map<Vec2, Long> {
    return moves.map { (move, count) ->
        val nextMoves = sequences[move]
        check(nextMoves != null) { "sequence for: $move not pre-calculated"}
        nextMoves.map { m ->
            m to count
        }
    }.flatten().groupByPair().mapValues { it.value.sum() }
}

private fun generateDirPadSequenceMap(): Map<Vec2, List<Vec2>> {
    val moves = (-2..2).map { x ->
        (-1..1).map { y ->
            Pair(x, y).toVec2()
        }
    }.flatten()

    moves - listOf(Vec2(2, 0), Vec2(2, 1)) // these moves should never happen on the dir pad (because it would mean starting from the gap)

    return moves.map { move ->
        move to calcNextDirPadMoves(move)
    }.toMap()
}

private fun calcNextDirPadMoves(delta: Vec2): List<Vec2> {
    var start = Vec2(2, 0)
    val forwardMoves = delta.toSteps().map { dir ->
        val c = dirToChar[dir]!!
        val end = dirPadMap[c]!!
        val d = (end - start)
        start = end
        d
    }

    val backwardsMove = (Vec2(2, 0) - start)

    return forwardMoves + backwardsMove
}

private fun Vec2.formatted() = listOf(this).formatted()
private fun List<Vec2>.formatted() = map { move ->
    move.toSteps().map { dir ->
        dirToChar[dir]!!
    } + 'A'
}.flatten().joinToString("")
private fun Map<Vec2, Long>.formatted() = map { (m, c) ->
    (1..c).map { m }
}.flatten().formatted()

val numPadMap = mapOf(
    '7' to Vec2(0, 0), '8' to Vec2(1, 0), '9' to Vec2(2, 0),
    '4' to Vec2(0, 1), '5' to Vec2(1, 1), '6' to Vec2(2, 1),
    '1' to Vec2(0, 2), '2' to Vec2(1, 2), '3' to Vec2(2, 2),
    /*     gap      */ '0' to Vec2(1, 3), 'A' to Vec2(2, 3)
)

val dirPadMap = mapOf(
    /*     gap      */ '^' to Vec2(1, 0), 'A' to Vec2(2,0),
    '<' to Vec2(0, 1), 'v' to Vec2(1, 1), '>' to Vec2(2, 1),
)

val dirToChar = mapOf(
    Vec2(0, 0) to 'A',
    Vec2(-1, 0) to '<',
    Vec2(1, 0) to '>',
    Vec2(0, -1) to '^',
    Vec2(0, 1) to 'v',
)

private fun Vec2.toSteps(yfirst: ()->Boolean = { x < 0 }): List<Vec2> {
    val xsteps = (1..abs(x)).map {
        Vec2(1, 0) * sign(x)
    }

    val ysteps = (1..abs(y)).map {
        Vec2(0, 1) * sign(y)
    }

   return if (yfirst()) ysteps + xsteps else xsteps + ysteps

}