package utils

import java.io.File

inline fun packageName(noinline block: () -> Unit): String = block.javaClass.packageName

/**
 * Reads lines from the given input txt file.
 */
fun readLines(name: String): List<String> = File("src").walkBottomUp().first { it.path.contains("$name.txt") }.readText().trim().lines()


/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

