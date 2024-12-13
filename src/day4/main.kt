package day4

import utils.*

fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 18,
        "input" to 2406,
    ))

    ::part2.runTests(day, listOf(
        "test" to 9,
        "input" to 1807,
    ))
}

fun part1Patterns(n: Int) = listOf(
    "(?=(XMAS|SAMX))",
    "(?=(X.{$n}M.{$n}A.{$n}S|S.{$n}A.{$n}M.{$n}X))",
    "(?=(X.{${n+1}}M.{${n+1}}A.{${n+1}}S|S.{${n+1}}A.{${n+1}}M.{${n+1}}X))",
    "(?=(X.{${n-1}}M.{${n-1}}A.{${n-1}}S|S.{${n-1}}A.{${n-1}}M.{${n-1}}X))"
).map { it.toRegex(RegexOption.DOT_MATCHES_ALL) }

private fun part1(input: List<String>): Int {
    val patterns = part1Patterns(input[0].length)
    return patterns.countAll(input.joinToString("\n"))
}

fun part2Patterns(n: Int) = listOf(
    "(?=(M|S)\\S(M|S).{${n-1}}(A).{${n-1}}((?<=M.{${(n-1)*2+1}})S|(?<=S.{${(n-1)*2+1}})M)\\S((?<=M.{${(n-1)*2+5}})S|(?<=S.{${(n-1)*2+5}})M))"
).map { it.toRegex(RegexOption.DOT_MATCHES_ALL) }

private fun part2(input: List<String>): Int {
    val patterns = part2Patterns(input[0].length)
    return patterns.countAll(input.joinToString("\n"))
}

fun List<Regex>.countAll(s: String) = this.sumOf {
    it.findAll(s).count()
}
