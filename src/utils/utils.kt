package utils

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

inline fun packageName(noinline block: () -> Unit): String = block.javaClass.packageName

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String): List<String> = File("src").walkBottomUp().first { it.path.contains("$name.txt") }.readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun String.splitToInt() = split(Regex("\\s+")).map { it.toInt() }

fun String.splitToLong() = split(Regex("\\s+")).map { it.toLong() }

fun List<String>.toInts() = map{ it.toInt() }
fun List<String>.toLongs() = map{ it.toLong() }

fun <T: Any>((List<String>) -> T).runTests(basePath: String, tc: List<Pair<String, T?>>) {
    tc.forEach { (path, expected) ->
        val input = readInput("$basePath/$path")
        val got = this.invoke(input)
        if (expected != null) {
            check(got == expected) {
                println("Failed to get the correct input for $path, expected: $expected got: $got")
            }
        } else {
            println("got $got for $path")
        }
    }
}