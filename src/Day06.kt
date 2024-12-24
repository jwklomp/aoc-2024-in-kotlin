import kotlinx.coroutines.*
import java.util.concurrent.Executors

data class PathElement(val cell: Cell<String>, val direction: Direction)

fun main() {
    val start = "^"
    val obstacle = "#"
    val startDirection = Direction.UP

    fun makeFloorGrid(input: List<String>): Grid2D<String> {
        val chunked = input.filterNot { it.isEmpty() }.map { it.chunked(1).toMutableList() }
        return Grid2D(chunked)
    }

    fun determinePath(input: List<String>): List<Cell<String>> {
        val floorGrid = makeFloorGrid(input)
        val allCells = floorGrid.getAllCells()
        val startCell = allCells.first { it.value == start }

        tailrec fun step(
            cell: Cell<String>,
            direction: Direction,
            path: List<Cell<String>>,
        ): List<Cell<String>> {
            // Return if the next step is outside the grid
            when (direction) {
                Direction.UP -> if (cell.y == 0) return path + cell
                Direction.DOWN -> if (cell.y == floorGrid.getNrOfRows() - 1) return path + cell
                Direction.LEFT -> if (cell.x == 0) return path + cell
                Direction.RIGHT -> if (cell.x == floorGrid.getNrOfColumns() - 1) return path + cell
            }

            // Get the next cell based on the current direction
            val nextCell = when (direction) {
                Direction.UP -> floorGrid.getCell(cell.x, cell.y - 1)
                Direction.DOWN -> floorGrid.getCell(cell.x, cell.y + 1)
                Direction.LEFT -> floorGrid.getCell(cell.x - 1, cell.y)
                Direction.RIGHT -> floorGrid.getCell(cell.x + 1, cell.y)
            }

            // If the next cell is an obstacle, turn right and continue from the current cell
            return if (nextCell.value == obstacle) {
                val nextDirection = direction.turnRight()
                step(cell, nextDirection, path) // Tail-recursive call
            } else {
                // Otherwise, move to the next cell and continue the path
                step(nextCell, direction, path + cell) // Tail-recursive call
            }
        }

        return step(startCell, startDirection, listOf(startCell)) // Start cell is part of the path
    }

    fun isLoop(floorGrid: Grid2D<String>, startCell: Cell<String>, extraObstacle: Cell<String>): Boolean {
        println("Start processing cell: $extraObstacle")
        tailrec fun step(
            cell: Cell<String>,
            direction: Direction,
            path: List<PathElement>,
        ): Boolean {
            // if the current cell has been visited with the same direction, we have a loop, return true
            if (path.any { it.cell == cell && it.direction == direction }) return true

            // Return false if the next step is outside the grid, no loop
            when (direction) {
                Direction.UP -> if (cell.y == 0) return false
                Direction.DOWN -> if (cell.y == floorGrid.getNrOfRows() - 1) return false
                Direction.LEFT -> if (cell.x == 0) return false
                Direction.RIGHT -> if (cell.x == floorGrid.getNrOfColumns() - 1) return false
            }

            // Get the next cell based on the current direction
            val nextCell = when (direction) {
                Direction.UP -> floorGrid.getCell(cell.x, cell.y - 1)
                Direction.DOWN -> floorGrid.getCell(cell.x, cell.y + 1)
                Direction.LEFT -> floorGrid.getCell(cell.x - 1, cell.y)
                Direction.RIGHT -> floorGrid.getCell(cell.x + 1, cell.y)
            }

            // If the next cell is an obstacle, or the extraObstacle, turn right and continue from the current cell
            return if (nextCell.value == obstacle || nextCell == extraObstacle) {
                val nextDirection = direction.turnRight()
                step(cell, nextDirection, path) // Tail-recursive call
            } else {
                // Otherwise, move to the next cell and continue the path
                step(nextCell, direction, path + PathElement(cell, direction)) // Tail-recursive call
            }
        }
        return step(startCell, startDirection, listOf())
    }

    fun part1(input: List<String>): Int {
        return determinePath(input).distinct().size
    }

    fun part2(input: List<String>): Int {
        val floorGrid = makeFloorGrid(input)
        val allCells = floorGrid.getAllCells()
        val startCell = allCells.first { it.value == start }
        val allEmptyCells = floorGrid.getAllCells().filter { it.value == "." }
        // Grid is not fast, so use coroutines to speed up the process
        val coreCount = Runtime.getRuntime().availableProcessors()
        val dispatcher = Executors.newFixedThreadPool(coreCount).asCoroutineDispatcher()
        return runBlocking(dispatcher) {
            val loopChecks = allEmptyCells.map { cell ->
                async {
                    isLoop(floorGrid, startCell, cell)
                }
            }

            // Wait for all results
            loopChecks.awaitAll().count { it }
        }
    }

    val testInput = readInput("Day06_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
