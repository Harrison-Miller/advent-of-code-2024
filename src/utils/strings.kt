package utils

fun String.splitToInts(sep: Regex = Regex("\\s+")) = trim().split(sep).map { it.toInt() }

fun String.splitToLongs(sep: Regex = Regex("\\s+")) = trim().split(sep).map { it.toLong() }

fun List<String>.toInts() = map{ it.trim().toInt() }
fun List<String>.toLongs() = map{ it.trim().toLong() }