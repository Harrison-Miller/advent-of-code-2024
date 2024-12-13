package utils

import kotlin.math.sqrt

typealias Vec2 = Pair<Int,Int>

val Vec2.x: Int
    get() = this.first

val Vec2.y: Int
    get() = this.second

operator fun Vec2.plus(b: Vec2) = Pair(x + b.x, y + b.y)
operator fun Vec2.minus(b: Vec2) = Pair(x - b.x, y - b.y)
operator fun Vec2.times(b: Int) = Pair(x*b, y*b)
fun Vec2.mag() = sqrt((x*x + y*y).toDouble())

fun Vec2.up(): Vec2 {
    return Vec2(x, y-1)
}

fun Vec2.down(): Vec2 {
    return Vec2(x, y+1)
}

fun Vec2.left(): Vec2 {
    return Vec2(x-1, y)
}

fun Vec2.right(): Vec2 {
    return Vec2(x+1, y)
}