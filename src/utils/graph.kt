package utils

import java.util.*

typealias AdjacencyMatrix<T> = Map<T, List<Pair<T, Long>>>

typealias Graph<T> = Pair<Set<T>, AdjacencyMatrix<T>>

val <T> Graph<T>.nodes: Set<T>
    get() = this.first

val <T> Graph<T>.adjacencyMatrix: AdjacencyMatrix<T>
    get() = this.second

inline fun <T: Any> Set<T>.adjacencyMatrix(weight: (a: T, b: T) -> Long?): AdjacencyMatrix<T> {
    return mapNotNull { a ->
        val e = mapNotNull { b ->
            weight(a, b)?.let { b to it }
        }
        if (e.isNotEmpty()) a to e else null
    }.groupByPair().mapValues { it.value.flatten() }
}

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
