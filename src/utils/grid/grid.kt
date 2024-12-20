package utils.grid

import utils.vec2.*

fun Vec2.inBounds(width: Int, height: Int): Boolean {
    return x >= 0 && x < width && y >= 0 && y < height
}

fun Iterable<Vec2>.filterInBounds(width: Int, height: Int) =
    filter { it.inBounds(width, height) }

typealias Grid<T> = List<List<T>>

val <T> Grid<T>.width: Int
    get() = this.first().size

val <T> Grid<T>.height: Int
    get() = this.size

fun <T> Grid<T>.filter(predicate: (T) -> Boolean): Set<Vec2> {
    return mapIndexed { y, row ->
        row.mapIndexedNotNull { x, cell ->
            if (predicate(cell)) Pair(x, y).toVec2() else null
        }
    }.flatten().toSet()
}

fun <T> Grid<T>.get(x: Int, y: Int, zero: T): T {
    return if (x >= 0 || x < width || y >= 0 || y < height) {
        get(y)?.get(x) ?: zero
    } else {
        zero
    }
}

fun <T> List<String>.mapGrid(transform: (Char) -> T): Grid<T> {
    return mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            transform(c)
        }
    }
}

fun <T> Grid<T>.render(displayCell: (T, Vec2) -> Char) {
    mapIndexed { y, row ->
        row.mapIndexed { x, cell ->
            val c = displayCell(cell, Pair(x, y).toVec2())
            print(c)
        }
        println()
    }
}



fun Iterable<Vec2>.render(width: Int, height: Int, displayCell: (Boolean, Vec2) -> Char?) {
    (0..<height).forEach { y ->
        (0..<width).forEach { x ->
            val pos = Pair(x, y).toVec2()
            val c = displayCell(contains(pos), pos)?: '.'
            print(c)
        }
        println()
    }
}

