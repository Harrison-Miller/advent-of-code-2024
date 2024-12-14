package utils.geometry

import utils.vec2.*

data class Triangle(
    val a: Vec2,
    val b: Vec2,
    val c: Vec2,
)

fun Triangle.inside(p: Vec2): Boolean {
    val crossAB = (a - b) * (a - p)
    val crossBC = (b - c) * (b - p)
    val crossCA = (c - a) * (c - p)

    // Point is inside if all cross products have the same sign or are zero
    return (crossAB >= 0 && crossBC >= 0 && crossCA >= 0) || (crossAB <= 0 && crossBC <= 0 && crossCA <= 0)
}
