package day19

import utils.*

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 6,
        "input" to 315,
    ))

    ::part2.runTests(day, listOf(
        "test" to 16,
        "input" to null,
    ))
}

private fun part1(lines: List<String>): Int {
    val towels = lines[0].split(",").map{ it.trim() }.toTowelRegex()
    return lines.drop(2).count{ towels.matches(it) }
}

private fun List<String>.toTowelRegex() = joinToString(separator = "|", prefix = "^(", postfix=")+$").toRegex()

private fun part2(lines: List<String>): Long {
    val towels = lines[0].split(",").map { it.trim() }
    val r = towels.toTowelRegex()
    // filter out designs that have possible solutions
    // then get only the towels that could possibly be used to build a design
    val designToTowels = lines.drop(2).filter{ r.matches(it) }.map { design ->
        val possibleTowels = towels.filter { design.contains(it) }
        design to possibleTowels
    }

    return designToTowels.map { (design, towels) ->
        findAllCombinations(design, towels)
    }.sum()
}

private fun findAllCombinations(design: String, towels: List<String>): Long {
    val matches = mutableMapOf<String, Long>()
    (0..design.lastIndex).forEach { end ->
        val currentDesign = design.substring(0..end)

        val totalWithPreviousMatches = matches.map { (partialMatch, count) ->
            val partialDesign = currentDesign.substringAfter(partialMatch)
            if (towels.contains(partialDesign)) count else 0
        }.sum()

        val total = totalWithPreviousMatches + towels.count { currentDesign == it }
        if (total > 0L) {
            matches[currentDesign] = total
        }

    }

    return matches[design] ?: 0
}