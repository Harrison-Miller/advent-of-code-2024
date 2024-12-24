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

A -> 2 (need more data, but <^A is my current implementation)
2 -> 9 should be ^^> but is >^^
3 -> 4 (need more data but 1 less up that 3->7 which we know is correct: current <<^)
4 -> 9 (need more data currently >>^A)
6 -> 8 (need more data currently <^A)
8 -> 6 (need more data currently v>A)
8 -> A (need more data currently vvv>A)

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
////        "single" to null
//    ))

    ::part2.runTests(day, listOf(
        "input" to 217698355426872,
    ))
}


private fun part1(lines: List<String>): Long {
//    numPadMap.forEach { (sc, start) ->
//        numPadMap.forEach { (ec, end) ->
//            if (sc != ec) {
//                print("$sc -> $ec = ")
//                calcNextNumPadMoves(start, end)
//            }
//        }
//    }

    val dirPadMoves = generateDirPadSequenceMap()
//    dirPadMoves.forEach { (delta, moves) ->
//        println("$delta = $moves")
//        println("${delta.formatted()} = ${moves.formatted()}")
//    }
    return lines.sumOf { code ->
        val x = code.trimStart('0').trimEnd('A').toLong()
//        val x = 1
        val moves = calcRobotMoves(dirPadMoves, code, 2, numPadMap['A']!!)
        x * moves
    }
//    return 0
}


private fun part2(lines: List<String>): Long {
    val dirPadMoves = generateDirPadSequenceMap()
    dirPadMoves.forEach { (delta, moves) ->
        println("$delta = $moves")
        println("${delta.formatted()} = ${moves.formatted()}")
    }
    val answer = lines.sumOf { code ->
        val x = code.trimStart('0').trimEnd('A').toLong()
        val moves = calcRobotMoves(dirPadMoves, code, 25, numPadMap['A']!!)
        x * moves
    }

//    check(answer in 212160856163173..212555956788389) { "answer not in expected range: $answer" }

    return answer
}

private fun calcRobotMoves(dirPadMoves: Map<Vec2, List<Vec2>>, code: String, depth: Int = 2, s: Vec2 = Vec2(2, 3)): Long {
    var start = s
    var moves = code.map { c ->
        var end = numPadMap[c]!!
        // generate moves for first dir pad
        val moves = calcNextNumPadMoves(start, end)
//        (end - start).formatted().println()
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

//            moves.println()
//        moves.formatted().println()

    return moves.map { (m, c) ->
        (m.cartesianLength()+1) * c
    }.sum()
}

private fun numPadOrdering(d: Vec2, s: Vec2, e: Vec2): Boolean {
    return (s.y == 3L && e.x == 0L) ||
            (abs(d.y) > 0 && !(s.x == 0L && e.y == 3L) && d.x >= 0) ||
            (d.x == 1L && d.y == -2L)

    // && !(s.x == 0L && e.y == 3L)
}

private fun calcNextNumPadMoves(numPadStart: Vec2, numPadEnd: Vec2): List<Vec2> {
    val delta = numPadEnd - numPadStart

    var dirPadStart = Vec2(2, 0)
//    delta.formatted{numPadOrdering(delta, numPadStart, numPadEnd)}.println()
    val forwardMoves = delta.toSteps{
        numPadOrdering(delta, numPadStart, numPadEnd)
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


    // these moves should never happen on the dir pad (because it would mean starting from the gap)
    val seqs = (moves - setOf(Vec2(2, 0), Vec2(2, 1))).associateWith { move ->
        calcNextDirPadMoves(move)
    }.toMutableMap()

    // the above code makes this go over the gap with <A<vAA>>^A
    // we can't differentiate <v and v< (both (-1, 1)) with this encoding
    // so replace it with an unused value and hand code the response
    seqs[Vec2(-2, -1)] = listOf(Vec2(-1, 0), Vec2(-100, 100), Vec2(0, 0), Vec2(2, -1))
    seqs[Vec2(-100, 100)] = listOf(Vec2(-1, 1), Vec2(-1, 0), Vec2(2, -1))

    // similarly <^A expands to v<<A^>A>A which goes over the gap
    // again we can't differentiate ^> adn >^ (both (1, -1))
    seqs[Vec2(-1, -1)] = listOf(Vec2(-2, 1), Vec2(100, -100), Vec2(1, 0))
    seqs[Vec2(100, -100)] = listOf(Vec2(0, 1), Vec2(-1, -1), Vec2(1, 0))
//    seqs[Vec2(100, -100)] = listOf(Vec2(-1, 0), Vec2(1, 1), Vec2(0, -1))

    return seqs
}


// we can't differentiate
// <v v< in our code and both are valid
// we need a new encoding for the map
private fun calcNextDirPadMoves(delta: Vec2): List<Vec2> {
    var start = Vec2(2, 0)
    val forwardMoves = delta.toSteps().map { dir ->
        val c = dirToChar[dir]!!
        val end = dirPadMap[c]!!
        var d = (end - start)
        start = end
//        if (start == Vec2(0, 0)) { // check for the gap
//            swapNext = true
//            swapped = dir
//            println("dir: $dir")
//            when (dir) {
//                Vec2(-1, 0) -> {
//                    d = Vec2(0, 1)
//                    start = Vec2(1, 1)
//                }
//                Vec2(0, -1) -> {
//                    d = Vec2(1, 0)
//                    start = Vec2(1, 1)
//                }
//            }
//        }

        check(start != Vec2(0,0)) {"generated directions that put us in the gap"}
        d
    }

    val backwardsMove = (Vec2(2, 0) - start)

    return forwardMoves + backwardsMove
}

private fun Vec2.formatted(yfirst: (Vec2)->Boolean = ::dirPadOrdering) = listOf(this).formatted(yfirst)
private fun List<Vec2>.formatted(yfirst: (Vec2)->Boolean = ::dirPadOrdering) = map { move ->
    if (move == Vec2(-100, 100)) {
        listOf('v', '<', 'A')
    } else if (move == Vec2(100, -100)) {
        listOf('>', '^', 'A')
    } else {
        move.toSteps(yfirst).map { dir ->
            dirToChar[dir]!!
        } + 'A'
    }
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

private fun dirPadOrdering(d: Vec2): Boolean {
    return d.x < -1 || (abs(d.y) == 1L && d.x == 1L)
}

//priority is <v^>
// <v
// <^
// ^>
//

// << comes last x < -1
// >> comes firs
private fun Vec2.toSteps(yfirst: (Vec2)->Boolean = ::dirPadOrdering): List<Vec2> {
    val xsteps = (1..abs(x)).map {
        Vec2(1, 0) * sign(x)
    }

    val ysteps = (1..abs(y)).map {
        Vec2(0, 1) * sign(y)
    }

   return if (yfirst(this)) ysteps + xsteps else xsteps + ysteps

}