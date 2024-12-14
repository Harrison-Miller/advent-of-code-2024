package utils.vec2

import kotlin.math.sqrt

fun Pair<Number, Number>.toVec2() = Vec2(first.toLong(), second.toLong())

typealias Vec2 = Pair<Long, Long>

val Vec2.x: Long
    get() = this.first

val Vec2.y: Long
    get() = this.second

operator fun Vec2.plus(b: Vec2) = Vec2(x + b.x, y + b.y)
operator fun Vec2.minus(b: Vec2) = Vec2(x - b.x, y - b.y)
operator fun Vec2.times(b: Int): Vec2 = Vec2(x*b, y*b)

fun Vec2.mag() = sqrt((x*x + y*y).toDouble())

fun Vec2.up(): Vec2 = Vec2(x, y-1)
fun Vec2.down(): Vec2 = Vec2(x, y+1)
fun Vec2.left(): Vec2 = Vec2(x-1, y)
fun Vec2.right(): Vec2 = Vec2(x+1, y)