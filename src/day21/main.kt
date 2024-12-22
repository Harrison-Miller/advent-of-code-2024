package day21

import utils.*
import utils.vec2.*
import kotlin.math.abs

/* Observations:
Shortest path doesn't matter, the shortest path will always just be any path directly between the two points i.e) the delta of start end
A sequences always ends on A and begins on A
Sequences repeat and there is a finite number of them (determined by the size of the number pad)
The order of moves (shortest path) doesn't matter since we always have to return to A
Going over the gap doesn't matter, because order doesn't matter.
 */

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

/*
33 213 1201 = 16
<A v<A ^>>A
11 121 3012 = 12
>A >^A <<vA

^^^<<A to <AAAv<AA^>>A correct but goes over gap
^^<<A to <AAv<AA^>>A correct but goes over gap
^<<A to <Av<AA^>>A correct but goes over gap
<<A to v<<AA^>>A correct but goes over gap
v<<A to v<A<AA^>>A correct but goes over gap
vv<<A to v<AA<AA^>>A correct but goes over gap
vvv<<A to v<AAA<AA^>>A correct but goes over gap
^^^<A to <AAAv<A^>>A correct but goes over gap
^^<A to <AAv<A^>>A correct but goes over gap
^<A to <Av<A^>>A correct but goes over gap
<A to v<<A^>>A correct but goes over gap
v<A to v<A<A^>>A correct but goes over gap
vv<A to v<AA<A^>>A correct but goes over gap
vvv<A to v<AAA<A^>>A correct but goes over gap

^^^A to <AAA>A correct
^^A to <AA>A correct
^A to <A>A correct

A to A correct/it depends

vA to v<A^>A correct, ^> maybe could go over gap if we're considering starting from <
vvA to v<AA^>A correct
vvvA to v<AAA^>A correct

^^^>A to <AAAv>A^A correct
^^>A to <AAv>A^A correct
^>A to <Av>A^A correct
>A to vA^A correct
v>A to v<A>A^A correct
vv>A to v<AA>A^A correct
vvv>A to v<AAA>A^A correct
^^^>>A to <AAAv>AA^A correct
^^>>A to <AAv>AA^A correct
^>>A to <Av>AA^A correct
>>A to vAA^A correct
v>>A to v<A>AA^A correct
vv>>A to v<AA>AA^A correct
vvv>>A to v<AAA>AA^A correct

 */


fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "single" to null
//        "single.txt" to 1972,
//        "input" to null,
    ))

//    ::part2.runTests(day, listOf(
//        "test" to 0,
//        "input" to null,
//    ))
}

/*
12 + 27 + 11 + 18

3 -> 7

^^<<A

<A|A|v<A|A|^>>A
^  ^  <  < A

v<<A|^>>A|A|v<A|<A|^>>A|A|<A|v>A|A|^A
<     A   A | v   <  A   A | ^  >  > A
^^              <<             A
v<A|<A|A|>>^A|A|vA|<^A|>A|A|vA|^A
v    < <  A   A >   ^  A  A  > A
   <<         ^^

v<<A>>^A>>^AAAAv<A<AvA^<A>A
 */

/*
379A
A -> 3
yfirst -> 12
xfirst -> 12

3 -> 7
yfirst -> 27
xfirst -> 23

7 -> 9
yfirst -> 11
xfirst -> 11

9 -> A
yfirst -> 18
xfirst -> 18
 */

// yfirst, xfirst
// 21030121011 = 12
// v<<AA^>AA>A
// [(-2, 1), (0, 0), (1, -1), (0, 0), (1, 0)]

// yfirst, xfirst
// 33021301201 = 16
// <AAv<AA^>>A
// [(-1, 0), (0, 0), (-1, 1), (0, 0), (2, -1)]

var yfirst = true

/*
68 * 29
60 * 980
68 * 179
64 * 456
64 * 379
 */

/*
456A
A->4
yfirst = 27
xfirst = 23

11
11

4->5
yfirst = 10
xfirst = 10

5->6
yfirst = 10
xfirst = 10

6->A
yfirst = 17
xfirst = 17
 */


//^^<<A
//33021303021 = 18
//<A|A|v<A|A|>>^A
//^ ^  <   < A
//21030121011 = 12
//v<<A|A|>^A|A|>A
// <   < ^  ^  A

private fun part1(lines: List<String>): Long {
    val sequences = generateSequenceMap()
    Vec2(-2, -2).formatted().println()
    sequences[Vec2(-2, -2)]!!.formatted().println()
    sequences[Vec2(-2, -2)]!!.println()
//    sequences.forEach { (m, s) ->
//        println("${m.formatted()} to ${s.formatted()}")
//    }
    return lines.map { code ->
//        val x = code.trimStart('0').trimEnd('A').toLong()
        val x = 1
        val moves = calcRobotMoves(sequences, "4", 2, numPadMap['A']!!)
//        println("code: $code, x: $x, moves: $moves")
        println("$moves * $x")
        x * moves
    }.sum()
//    return 0
}

private fun part2(lines: List<String>): Int {
    return 0
}

private fun calcRobotMoves(sequences: Map<Vec2, List<Vec2>>, code: String, depth: Int = 2, s: Vec2 = Vec2(2, 3)): Long {
    var start = s
    var moves = code.map { c ->
        var end = numPadMap[c]!!
        val d = end - start
        start = end
        d
    }.groupBy { it }.mapValues { it.value.count().toLong() }

//    moves.println()
//    moves.formatted().println()

    (1..depth).forEach {
        moves = nextMoves(sequences, moves)
//        moves.println()
        moves.formatted().println()
    }

    return moves.map { (m, c) ->
        (m.cartesianLength()+1) * c
    }.sum()
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

private fun generateSequenceMap(): Map<Vec2, List<Vec2>> {
    val moves = (-2..2).map { x ->
        (-3..3).map { y ->
            Pair(x, y).toVec2()
        }
    }.flatten()

    return moves.map { move ->
        move to calcNextMoves(move)
    }.toMap()
}

/*
//^^<<A
//33021303021 = 18
//<A|A|v<A|A|>>^A
//^ ^  <   < A
//21030121011 = 12
//v<<A|A|>^A|A|>A
// <   < ^  ^  A
// the checker obeys the don't go over a gap law so we must as well, even though it shouldn't matter
// all of our code works correctly with going left vertical first going right vertical second ( which solves it entirely).
// the only time we encounter sequences longer than 3 is on the number pad and that's where we run into trouble
// i.e) ^^ << A is slower than << ^^ A
// but from A -> 4 we can only do ^^ << A (slower)
// but from 3 -> 7 we can do << ^^ A (faster).
// Solution don't generate sequences for the numPad instead build them based on position
// everything else should be fine.
 */
private fun calcNextMoves(delta: Vec2): List<Vec2> {
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

private fun Vec2.toSteps(): List<Vec2> {
    val xsteps = (1..abs(x)).map {
        Vec2(1, 0) * sign(x)
    }

    val ysteps = (1..abs(y)).map {
        Vec2(0, 1) * sign(y)
    }

//    println("should swap: $this")
//    return if (this in listOf(Vec2(-1, 0), Vec2(-1, 1), Vec2(2, -1), Vec2(1, 0), Vec2(1, -1), Vec2(-2, 1))) xsteps + ysteps else ysteps + xsteps
//    return if (this == Vec2(-1, -2)) xsteps + ysteps else ysteps + xsteps
//    return xsteps + ysteps
//    return ysteps +
//    return if (this in listOf(Vec2(-2, -2))) xsteps + ysteps else ysteps + xsteps
//    return if (yfirst) ysteps + xsteps else xsteps + ysteps

    // moving right
   return if (x > 0) xsteps + ysteps else ysteps + xsteps

}

/*
should swap: (0, -1)
should swap: (-2, -2)
should swap: (2, 0)
should swap: (0, 3)
^A^^<<A>>AvvvA

should swap: (-1, 0)
should swap: (-1, 1)
should swap: (2, -1)
<A<A>AAAAAAv<Av<A^>>AvA^A^>A

33 213 1201 = 16
<A v<A ^>>A
11 121 3012 = 12
>A >^A <<vA

should swap: (1, 0)
should swap: (1, -1)
should swap: (-2, 1)
<A>A>A<<vAAAAAA>^A>^AvA^A<vA

should swap: (-2, 1)
should swap: (-2, 1)
should swap: (2, -1)
should swap: (2, -1)
should swap: (2, -1)
should swap: (2, -1)
should swap: (0, 1)
should swap: (0, -1)
should swap: (0, -1)
should swap: (0, -1)
should swap: (0, 0)
should swap: (0, 0)
should swap: (0, 0)
should swap: (0, 0)
should swap: (0, 0)
should swap: (0, 0)
should swap: (-1, 1)
should swap: (-1, 1)
should swap: (-1, 1)
should swap: (-1, 0)
should swap: (-1, 0)
should swap: (-1, 0)
should swap: (-1, 0)
should swap: (-1, 0)
should swap: (1, 1)
should swap: (1, 1)
should swap: (1, -1)
should swap: (1, 0)
v<<Av<<A^>>A^>>A^>>A^>>AvA^A^A^AAAAAAAv<Av<Av<A<A<A<A<A<Av>Av>A^>A>A

should swap: (-2, 1)
should swap: (-2, 1)
should swap: (-2, 1)
should swap: (2, -1)
should swap: (0, 1)
should swap: (0, 1)
should swap: (0, 1)
should swap: (0, 1)
should swap: (0, -1)
should swap: (0, -1)
should swap: (0, 0)
should swap: (0, 0)
should swap: (0, 0)
should swap: (0, 0)
should swap: (0, 0)
should swap: (0, 0)
should swap: (1, 0)
should swap: (1, 0)
should swap: (1, 0)
should swap: (1, 0)
should swap: (1, 0)
should swap: (1, -1)
should swap: (1, -1)
should swap: (1, -1)
should swap: (-1, -1)
should swap: (-1, -1)
should swap: (-1, 1)
should swap: (-1, 0)
<<vA<<vA<<vA>>^AvAvAvAvA^A^AAAAAAA>A>A>A>A>A>^A>^A>^A<^A<^A<vA<A

 */