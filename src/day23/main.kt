package day23

import utils.*


/*
aq,cg,yn


aq - yn,vc,cg,wg
cg - de,tb,yn,aq
yn - aq,cg,wh,td

for each triplet make a set and compare to the sets for each member of the triple, if all of the triplets contain the set
i.e) triple set union each other set same length

build triplets by iterating through every pair and adding each member from its connection list

part 2 likely asks you to find a lan party of size X, creating combinations of X to check ill become difficult.

We can start by filtering all computers that don't have at least X connections, but The test data appears to have 4 connections for every computer (bigger than X=3)

We can also filter by only building our X-tuples by starting with computers that have a t in the name

 */


// Guesses:
// 2275 to high
fun main() {
    val day = packageName{}
    ::part1.runTests(day, listOf(
        "test" to 7,
        "input" to 1077,
    ))

    ::part2.runTests(day, listOf(
        "test" to "co,de,ka,ta",
        "input" to null,
    ))
}


private fun part1(lines: List<String>): Long {
    val connections = lines.map {
        val (l, r) = it.split("-")
        listOf(
            l to setOf(r),
            r to setOf(l)
        )
    }.flatten().groupByPair().mapValues { it.value.flatten().toSet() }

    val pruned = connections.filter {
        it.key.startsWith('t') || it.value.any { it.startsWith('t') }
    }

    val parties = findLanParties(pruned, 3) { it.key.startsWith('t') }

    return parties.count().toLong()
}

private fun part2(lines: List<String>): String {
    val connections = lines.map {
        val (l, r) = it.split("-")
        listOf(
            l to setOf(r),
            r to setOf(l)
        )
    }.flatten().groupByPair().mapValues { it.value.flatten().toSet() }

    val maxConnections = connections.toList().first().second.size

    (4..maxConnections).reversed().forEach { n ->
        val parties = findLanParties(connections, n)
        check(parties.size < 2) { "more than one solution at size: $n" }

        if (parties.size == 1) {
            return parties.single().sorted().joinToString(",")
        }
    }

    check(false) { "no solutions of size 4 or greater" }
    return ""
}

private fun findLanParties(connections: Map<String, Set<String>>, n: Int, hostPredicate: (Map.Entry<String, Set<String>>) -> Boolean = { true }): Set<Set<String>> {
    val parties = connections.filter(hostPredicate).map { (com, adj) ->
        val possibleParties = adj.combinations(n-1).map { it.toSet() }.toSet()
        val possibleMembers = connections.filter { com in it.value }

        possibleParties.filter { party ->
            possibleMembers.filter { it.key in party }.all { ((party - it.key) - it.value).isEmpty() }
        }.map { it + com }.toSet()
    }.flatten().toSet()

    return parties
}