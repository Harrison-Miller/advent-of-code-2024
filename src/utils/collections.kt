package utils

inline fun <T, K> Iterable<T>.countBy(keySelector: (T) -> K): Map<K, Int> {
    return groupBy(keySelector).mapValues { it.value.size }
}