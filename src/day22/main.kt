package day22

import utils.*

fun main() {
    val day = packageName{}
//    ::part1.runTests(day, listOf(
//        "test" to 37327623,
//        "input" to null,
//    ))

    ::part2.runTests(day, listOf(
        "test2" to 23,
        "input" to null,
    ))
}

private fun part1(lines: List<String>): Long {
    return lines.toLongs().map { s ->
        mrand(s, 2000).last()
    }.sum()
}

private fun part2(lines: List<String>): Long {
    val secrets = lines.toLongs().map { seed ->
        seed to mrand(seed, 2000).map { it%10 }
    }

    val allSequences = secrets.map { generateSequences(it) }

    return findMaxSequence(allSequences)
}

private fun generateSequences(secrets: Pair<Long, List<Long>>): Map<List<Long>, Long> {
    val (seed, nums) = secrets
    var prev = seed%10
    val diffSequences = nums.map {
        val diff = (it - prev)
        prev = it
        it to diff // price to diff with previous
    }

//    diffSequences.println()

    val seqToSellPrice = diffSequences.windowed(4).map {
        val seq = it.map { it.second } // sequence of diffs
        val sellPrice = it.last().first // final price at end of sequence
        seq to sellPrice
    }.groupByPair().mapValues { it.value.first() }.filter{ it.value > 0 } // only take the first seen value for each sequence

//    seqToSellPrice.println()

    return seqToSellPrice
}

private fun findMaxSequence(allSequences: List<Map<List<Long>, Long>>): Long {
    val sums = allSequences.map {
        it.toList()
    }.flatten().groupByPair().mapValues { it.value.sum() }


    //sums.println()

    val maxSeq = sums.maxBy { it.value }
    maxSeq.println()
    return maxSeq.value
}

//        var prev = s%10
//        mrand(s, 2000).map { it%10 }/*.map {
//            val pdiff = it to (it - prev)
//            prev = pdiff.first
//            pdiff
//      }//.windowed(4).groupBy { it }.mapValues { it.value.size }

//TODO: we can probably do this iteratively rather than all once
//private fun maximizeGains(repeatingDiffs: List<Map<List<Pair<Long, Long>>, Int>>): Long {
//    val commonSets = repeatingDiffs.map { diffs ->
//        val y = diffs.map { (k, v) ->
//            k to 1
//        }
//        y
//    }.flatten().groupByPair().mapValues { it.value.sum() }.filter { it.value > 1 }.also(::println)
//
//    commonSets.map { (sequence, count) ->
//        val sellPrice = sequence
//    }
//    return 0
//}

private fun mrand(s: Long, count: Int): List<Long> {
    var seed = s
    return (1..count).map {
        seed = monkeyRand(seed)
        seed
    }
}

// we're looking for the cycle that ends in the highest number

// this is very reminiscent of the opcode day
val m = 16777216
private fun monkeyRand(a: Long): Long {
    val b = a * 64 // shift left 6, makes the first 4 bits all 0
    // xor with all 0s so still a
    // does modulo change the first 4 bits, I don't think so?
    val c = (a xor b) % m


    val d = c / 32 // shift right 5, makes the top bit change of the 4 bit possibly change
    val e = (c xor d) % m

    val f = e * 2048 // shift left 11
    val g = (e xor f) % m

    return g
}

/*
0000 = 0
0001 = 1
0010 = 2
0011 = 3
0100 = 4
0101 = 5
0110 = 6
0111 = 7
1000 = 8
1001 = 9
1010 = 10 = 0 % 10
etc.
 */