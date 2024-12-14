package utils.vec2

import kotlin.math.abs
import kotlin.math.sqrt

fun Pair<Number, Number>.toVec2() = Vec2(first.toLong(), second.toLong())
fun String.toVec2(): Vec2 {
    val (x, y) = split(",")
    return Vec2(x.trim().toLong(), y.trim().toLong())
}


typealias Vec2 = Pair<Long, Long>

val Vec2.x: Long
    get() = this.first

val Vec2.y: Long
    get() = this.second

operator fun Vec2.plus(b: Vec2) = Vec2(x + b.x, y + b.y)
operator fun Vec2.minus(b: Vec2) = Vec2(x - b.x, y - b.y)

@JvmName("Vec2TimesInt")
operator fun Vec2.times(b: Int): Vec2 = Vec2(x*b, y*b)

@JvmName("Vec2TimesLong")
operator fun Vec2.times(b: Long): Vec2 = Vec2(x*b, y*b)

// cross product
operator fun Vec2.times(b: Vec2): Long = x*b.y - y*b.x

fun Vec2.mag() = sqrt((x*x + y*y).toDouble())

fun Vec2.up(): Vec2 = Vec2(x, y-1)
fun Vec2.down(): Vec2 = Vec2(x, y+1)
fun Vec2.left(): Vec2 = Vec2(x-1, y)
fun Vec2.right(): Vec2 = Vec2(x+1, y)

fun Vec2.wrap(b: Vec2): Vec2 {
    val x = ((x % b.x) + b.x) % b.x
    val y = ((y % b.y) + b.y) % b.y
    return Vec2(x, y)
}
