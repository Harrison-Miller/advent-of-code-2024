package utils

import kotlin.reflect.KFunction
import kotlin.time.TimeSource

data class TestConfig(
    val basePath: String,
    val benchmark: Boolean = false,
    val alwaysPrint: Boolean = true,
)

private val timeSource = TimeSource.Monotonic

fun <T: Any>((List<String>) -> T).runTests(basePath: String, tc: List<Pair<String, T?>>) = this.runTests(TestConfig(basePath), tc)

fun <T: Any>((List<String>) -> T).runTests(config: TestConfig, tc: List<Pair<String, T?>>) {
    tc.forEach { (path, expected) ->
        val testName = if (this is KFunction<*>) "${this.name}.$path" else path
        val input = readLines("${config.basePath}/$path")
        val startTime = timeSource.markNow()
        val got = this.invoke(input)
        val endTime = timeSource.markNow()

        if (config.benchmark) println("$testName took ${endTime-startTime}")

        if (expected != null) {
            check(got == expected) {
                println("Failed to get the correct input for $testName, expected: $expected got: $got")
            }

            if (config.alwaysPrint) println("got $got for $testName")
        } else {
            println("got $got for $testName")
        }
    }
}