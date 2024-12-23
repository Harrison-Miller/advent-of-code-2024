package utils

inline fun <T, K> Iterable<T>.countBy(keySelector: (T) -> K): Map<K, Int> {
    return groupBy(keySelector).mapValues { it.value.size }
}

fun <A, B> List<Pair<A, B>>.groupByPair(): Map<A, List<B>> {
    return groupBy(
        keySelector = { it.first },
        valueTransform = { it.second },
    )
}

fun <K, V> Map<K, Iterable<V>>.flipFlatten(): Map<V, K> {
    return map { (k, v) ->
        v.map {
            it to k
        }
    }.flatten().toMap()
}

fun <K, V> Map<K, V>.flipAndGroup(): Map<V, List<K>> {
    return map { (k, v) ->
        v to k
    }.groupByPair()
}

fun <K, V> Map<K, V>.flip(): Map<V, K> {
    return map { (k, v) ->
        v to k
    }.toMap()
}

inline fun <T> Set<T>.floodFill(flood: (T) -> Set<T>): Set<T> {
    val region = toMutableSet().apply { addAll(this) }
    var new = region.toSet()
    do {
        new = new.map(flood).flatten().toSet().subtract(region)
        region.addAll(new)
    } while(new.isNotEmpty())
    return region
}

fun <T> Iterable<T>.combinations(length: Int): Sequence<List<T>> =
    sequence {
        val pool = this@combinations as? List<T> ?: toList()
        val n = pool.size
        if(length > n) return@sequence
        val indices = IntArray(length) { it }
        while(true) {
            yield(indices.map { pool[it] })
            var i = length
            do {
                i--
                if(i == -1) return@sequence
            } while(indices[i] == i + n - length)
            indices[i]++
            for(j in i+1 until length) indices[j] = indices[j - 1] + 1
        }
    }