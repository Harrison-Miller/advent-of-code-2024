package utils.grid

import utils.vec2.*

fun Iterable<Vec2>.filterInBounds(width: Int, height: Int) =
    filter { it.x >= 0 && it.x < width && it.y >= 0 && it.y < height }

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

fun <T> List<String>.mapSparseGrid(transform: (Char) -> T?): SparseGrid<T> {
    return SparseGrid<T>(
        width = first().length,
        height = size,
        cells = mapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                val t = transform(c)
                val p = if (t != null) Pair(x, y).toVec2() to t else null
                p
            }
        }.flatten().toMap()
    )
}


data class SparseGrid<T>(
    val width: Int,
    val height: Int,
    val cells: Map<Vec2, T>
)

fun <T> Grid<T>.render(displayCell: (T, Vec2) -> Char) {
    mapIndexed { y, row ->
        row.mapIndexed { x, cell ->
            val c = displayCell(cell, Pair(x, y).toVec2())
            print(c)
        }
        println()
    }
}

fun <T> SparseGrid<T>.render(displayCell: (T?, Vec2) -> Char?) = cells.render(width, height, displayCell)

fun <T> Map<Vec2, T>.render(width: Int, height: Int, displayCell: (T?, Vec2) -> Char?) {
    (0..<height).map { y ->
        (0..<width).map { x ->
            val pos = Pair(x, y).toVec2()
            val cell = get(pos)
            val c = displayCell(cell, pos) ?: '.'
            print(c)
        }
        println()
    }
}

fun Iterable<Vec2>.render(width: Int, height: Int, displayCell: (Boolean, Vec2) -> Char?) {
    (0..<height).map { y ->
        (0..<width).map { x ->
            val pos = Pair(x, y).toVec2()
            val c = displayCell(contains(pos), pos)?: '.'
            print(c)
        }
        println()
    }
}

