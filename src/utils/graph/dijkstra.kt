package utils.graph

import utils.floodFill
import java.util.*

fun <T> dijkstra(adj: AdjacencyMatrix<T>, start: T): Map<T, Long> {
    val distances = mutableMapOf<T, Long>().withDefault { Long.MAX_VALUE }
    val priorityQueue = PriorityQueue<Pair<T, Long>>(compareBy { it.second }).apply { add(start to 0) }

    distances[start] = 0

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDist) = priorityQueue.poll()
        adj[node]?.forEach { (adjacent, weight) ->
            val totalDist = currentDist + weight
            if (totalDist < distances.getValue(adjacent)) {
                distances[adjacent] = totalDist
                priorityQueue.add(adjacent to totalDist)
            }
        }
    }
    return distances
}

fun <T> dijkstraWithPaths(adj: AdjacencyMatrix<T>, start: T): Map<T, Pair<Long, Set<T>>> {
    val distancesAndPaths = mutableMapOf<T, Pair<Long, Set<T>>>().withDefault { Long.MAX_VALUE to emptySet() }
    val priorityQueue = PriorityQueue<Pair<T, Pair<Long, Set<T>>>>(compareBy { it.second.first }).apply { add(start to (0L to emptySet())) }

    distancesAndPaths[start] = 0L to emptySet()

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDistAndPath) = priorityQueue.poll()
        adj[node]?.forEach { (adjacent, weight) ->
            val totalDist = currentDistAndPath.first + weight
            val adjDistAndPath = distancesAndPaths.getValue(adjacent)
            if (totalDist < adjDistAndPath.first) {
                val path = (currentDistAndPath.second + node)
                distancesAndPaths[adjacent] = totalDist to path
                priorityQueue.add(adjacent to (totalDist to path))
            }
        }
    }
    return distancesAndPaths
}


typealias NodePaths<T> = Map<T, Pair<Long, List<Set<T>>>>

fun <T> NodePaths<T>.getPaths(node: T): Set<T> {
    val paths = get(node)?.second?.flatten()?.toSet() ?: emptySet()
    return paths.floodFill {
        get(it)?.second?.flatten()?.toSet() ?: emptySet()
    }
}

fun <T> dijkstraWithAllPaths(adj: AdjacencyMatrix<T>, start: T): NodePaths<T> {
    val distancesAndPaths = mutableMapOf<T, Pair<Long, List<Set<T>>>>().withDefault { Long.MAX_VALUE to emptyList() }
    val priorityQueue = PriorityQueue<Pair<T, Pair<Long, List<Set<T>>>>>(compareBy { it.second.first }).apply { add(start to (0L to emptyList())) }

    distancesAndPaths[start] = 0L to emptyList()

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDistAndPath) = priorityQueue.poll()
        adj[node]?.forEach { (adjacent, weight) ->
            val totalDist = currentDistAndPath.first + weight
            val adjDistAndPath = distancesAndPaths.getValue(adjacent)
            if (totalDist == adjDistAndPath.first) {
                val paths = if(currentDistAndPath.second.isNotEmpty()) {
                    currentDistAndPath.second.map {
                        it + node
                    }
                } else {
                    listOf(setOf(node))
                } + adjDistAndPath.second
                distancesAndPaths[adjacent] = totalDist to paths
            } else if (totalDist < adjDistAndPath.first) {
                val paths = if(currentDistAndPath.second.isNotEmpty()) {
                    currentDistAndPath.second.map {
                        it + node
                    }
                } else {
                    listOf(setOf(node))
                }
                distancesAndPaths[adjacent] = totalDist to paths
                priorityQueue.add(adjacent to (totalDist to paths))
            }
        }
    }
    return distancesAndPaths
}

