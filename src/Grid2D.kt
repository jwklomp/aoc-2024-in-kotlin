import kotlin.math.abs

class Grid2D<T>(private val grid: List<List<T>>) {
    private val rowLength: Int = grid.first().size // corresponds to x
    private val columnLength: Int = grid.size // corresponds to y

    private val surrounding: List<Pair<Int, Int>> =
        listOf(Pair(-1, -1), Pair(-1, 0), Pair(-1, 1), Pair(0, -1), Pair(0, 1), Pair(1, -1), Pair(1, 0), Pair(1, 1))
    private val adjacent: List<Pair<Int, Int>> = listOf(Pair(-1, 0), Pair(0, -1), Pair(0, 1), Pair(1, 0))

    fun getNrOfRows(): Int = rowLength

    fun getNrOfColumns(): Int = columnLength

    fun getCell(x: Int, y: Int): Cell<T> = Cell(value = grid[y][x], x = x, y = y)

    fun getAllCells(): List<Cell<T>> =
        grid.flatMapIndexed { y, row -> row.mapIndexed { x, v -> Cell(value = v, x = x, y = y) } }

    fun getCellsFiltered(filterFn: (Cell<T>) -> (Boolean)): List<Cell<T>> = getAllCells().filter { filterFn(it) }

    fun getSurrounding(x: Int, y: Int): List<Cell<T>> = filterPositions(surrounding, x, y)

    fun getAdjacent(x: Int, y: Int): List<Cell<T>> = filterPositions(adjacent, x, y)

    fun getRow(rowNr: Int): List<Cell<T>> =
        getCellsFiltered { it.y == rowNr }.sortedBy { it.x } // row: x variable, y fixed

    fun getCol(colNr: Int): List<Cell<T>> =
        getCellsFiltered { it.x == colNr }.sortedBy { it.y } // row: y variable,x fixed

    fun isOnEdge(x: Int, y: Int) = x == 0 || y == 0 || x == rowLength - 1 || y ==  columnLength - 1

    fun clone(transform: (Cell<T>) -> T): Grid2D<T> {
        val clonedGrid = grid.mapIndexed { y, row ->
            row.mapIndexed { x, v -> transform(Cell(value = v, x = x, y = y)) }
        }
        return Grid2D(clonedGrid)
    }

    fun <T> cellToId(c: Cell<T>): String = "x${c.x}-y${c.y}"

    fun getXY(input: String): Pair<Int, Int>? {
        val regex = Regex("""x(\d+)-y(\d+)""")
        val matchResult = regex.find(input)

        return matchResult?.let {
            val (x, y) = it.destructured
            Pair(x.toInt(), y.toInt())
        }
    }

    private fun filterPositions(positions: List<Pair<Int, Int>>, x: Int, y: Int): List<Cell<T>> =
        positions
            .map { Pair(it.first + x, it.second + y) }
            .filter { it.first >= 0 && it.second >= 0 }
            .filter { it.first < rowLength && it.second < columnLength }
            .map { getCell(it.first, it.second) }

    override fun toString(): String {
        return grid.joinToString(separator = "\n") { row ->
            row.joinToString(separator = "\t") { it.toString() }
        }
    }
}

data class Cell<T>(var value: T, val x: Int, val y: Int)

typealias Node<T> = Cell<T>

// Determine if two cells are direct neighbors
fun <T> Cell<T>.isNeighborOf(u: Cell<T>): Boolean {
    val xDist = abs(this.x - u.x)
    val yDist = abs(this.y - u.y)
    return xDist + yDist == 1
}

/**
 * Use Shoelace formula to calculate the area of a simple polygon whose vertices are described
 * by their Cartesian coordinates in the plane. See https://www.101computing.net/the-shoelace-algorithm/
 */
fun calculateShoelaceArea(cells: List<Cell<*>>): Double {
    var area = 0.0

    cells.windowed(2) { (currentCell, nextCell) ->
        area += currentCell.x * nextCell.y - nextCell.x * currentCell.y
    }

    // Add the last edge
    val lastCell = cells.last()
    val firstCell = cells.first()
    area += lastCell.x * firstCell.y - firstCell.x * lastCell.y

    return (abs(area) / 2.0)
}
