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

    val maxSeq = sums.maxBy { it.value }
    maxSeq.println()
    return maxSeq.value
}


private fun mrand(s: Long, count: Int): List<Long> {
    var seed = s
    return (1..count).map {
        seed = monkeyRand(seed)
        seed
    }
}

val m = 16777216
private fun monkeyRand(a: Long): Long {
    val b = a * 64
    val c = (a xor b) % m


    val d = c / 32
    val e = (c xor d) % m

    val f = e * 2048
    val g = (e xor f) % m

    return g
}