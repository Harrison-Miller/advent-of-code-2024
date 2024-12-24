package utils.vec2

import kotlin.math.abs
import kotlin.math.sqrt

fun Pair<Number, Number>.toVec2() = Vec2(first.toLong(), second.toLong())
fun String.toVec2(): Vec2 {
    val (x, y) = split(",")
    return Vec2(x.trim().toLong(), y.trim().toLong())
}

object Directions {
    val ZERO = Vec2(0,0)
    val ONE = Vec2(1,1)
    val LEFT = Vec2(-1,0)
    val WEST = Vec2(-1,0)
    val RIGHT = Vec2(1,0)
    val EAST = Vec2(1,0)
    val UP = Vec2(0,-1)
    val NORTH = Vec2(0,-1)
    val DOWN = Vec2(0,1)
    val SOUTH = Vec2(0,1)
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

operator fun Vec2.div(b: Double): Vec2 = Vec2((x.toDouble()/b).toLong(), (y.toDouble()/b).toLong())

// cross product
operator fun Vec2.times(b: Vec2): Long = x*b.y - y*b.x

fun Vec2.mag() = sqrt((x*x + y*y).toDouble())
fun Vec2.normalize() = this / mag()

fun Vec2.dirTo(b: Vec2): Vec2 {
    val dir = b - this
    return dir.normalize()
}

fun Vec2.cartesianDistTo(b: Vec2) = (this - b).cartesianLength()

fun Vec2.cartesianLength() = abs(x) + abs(y)

fun Vec2.up(): Vec2 = Vec2(x, y-1)
fun Vec2.down(): Vec2 = Vec2(x, y+1)
fun Vec2.left(): Vec2 = Vec2(x-1, y)
fun Vec2.right(): Vec2 = Vec2(x+1, y)

fun Vec2.wrap(b: Vec2): Vec2 {
    val x = ((x % b.x) + b.x) % b.x
    val y = ((y % b.y) + b.y) % b.y
    return Vec2(x, y)
}
