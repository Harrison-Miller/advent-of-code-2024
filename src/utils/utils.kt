package utils

import java.io.File
import kotlin.time.TimeSource

inline fun packageName(noinline block: () -> Unit): String = block.javaClass.packageName

/**
 * Reads lines from the given input txt file.
 */
fun readLines(name: String): List<String> = File("src").walkBottomUp().first { it.path.contains("$name.txt") }.readText().trim().lines()


/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)


private val timeSource = TimeSource.Monotonic
fun <T> elapsedTime(name: String = "something", print: Boolean = true, block: () -> T): T {
    val start = timeSource.markNow()
    val result = block()
    val end = timeSource.markNow()
    if (print) println("time elapsed for $name: ${end-start}")
    return result
}

fun Boolean.doif(block: () -> Unit) = if(this) block() else Unit



