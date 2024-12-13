package utils

fun String.splitToInts(sep: Regex = Regex("\\s+")) = split(sep).map { it.toInt() }

fun String.splitToLong(sep: Regex = Regex("\\s+")) = split(sep).map { it.toLong() }

fun List<String>.toInts() = map{ it.toInt() }
fun List<String>.toLongs() = map{ it.toLong() }