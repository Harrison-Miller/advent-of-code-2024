package utils.grid

import utils.groupByPair
import utils.vec2.Vec2
import utils.vec2.toVec2

data class SparseGrid<T>(
    val width: Int,
    val height: Int,
    val cells: Map<Vec2, T>
)

fun <T> SparseGrid<T>.dimensions(): Vec2 = Pair(width, height).toVec2()

@JvmName("mapLineToSparseGrid")
fun <T> List<String>.mapSparseGrid(transform: (String) -> Pair<Vec2, T>?): SparseGrid<T> {
    val cells = mapNotNull { line ->
        transform(line) ?: null
    }.toMap()

    return SparseGrid<T>(
        width = cells.maxOf { it.key.first }.toInt(),
        height = cells.maxOf { it.key.second }.toInt(),
        cells = cells,
    )
}

@JvmName("mapLineToListSparseGrid")
fun <T> List<String>.mapSparseGrid(transform: (String) -> Pair<Vec2, List<T>>?): SparseGrid<List<T>> {
    val cells = mapNotNull { line ->
        transform(line) ?: null
    }.groupByPair().mapValues { it.value.flatten() }

    return SparseGrid<List<T>>(
        width = cells.maxOf { it.key.first }.toInt() + 1,
        height = cells.maxOf { it.key.second }.toInt() + 1,
        cells = cells,
    )
}

@JvmName("mapCharToSparseGrid")
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
