package utils.geometry

import utils.vec2.*

data class Square(
    val a: Vec2,
    val width: Long,
    val height: Long,
)

fun Square.inside(p: Vec2): Boolean {
    return a.x <= p.x && a.x+width >= p.x && a.y <= p.y && a.y+height >= p.y
}