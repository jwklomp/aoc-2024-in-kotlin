fun main() {
    fun makeGrid(input: List<String>): Grid2D<String> =
        Grid2D(input.filterNot(String::isEmpty).map { it.chunked(1).toMutableList() })

    tailrec fun doMoves(
        warehouse: Grid2D<String>,
        robotCell: Cell<String>,
        moves: List<String>
    ) {
        if (moves.isEmpty()) return

        println("Move: ${moves.first()}")
        val cells = when (moves.first()) {
            ">" -> warehouse.getRow(robotCell.y)
            "<" -> warehouse.getRow(robotCell.y).reversed()
            "v" -> warehouse.getCol(robotCell.x)
            "^" -> warehouse.getCol(robotCell.x).reversed()
            else -> emptyList()
        }
        val involvedCells = cells.dropWhile { it != robotCell }.takeWhile { it.value != "#" }
        if (involvedCells.map { it.value }.contains(".")) {
            val updateCells = involvedCells.takeWhileInclusive { it.value != "." }
            updateCells.forEachIndexed { i, c ->
                if (i == 0) warehouse.setCell(c.x, c.y, ".")
                else warehouse.setCell(c.x, c.y, updateCells[i - 1].value)
            }
        }
        doMoves(warehouse, warehouse.getAllCells().first { it.value == "@" }, moves.drop(1))
    }

    fun part1(inputGrid: List<String>, inputMoves: List<String>): Long {
        val warehouse = makeGrid(inputGrid)
        val moves = inputMoves.joinToString("").chunked(1)
        val robotCell = warehouse.getAllCells().first { it.value == "@" }
        doMoves(warehouse, robotCell, moves)
        return warehouse.getAllCells().filter { it.value == "O" }.sumOf { it.x + (it.y * 100L) }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testGrid = readInput("Day15_test_grid")
    val testMoves = readInput("Day15_test_moves")
    part1(testGrid, testMoves).println()
    //part2(testInput).println()

    val grid = readInput("Day15_grid")
    val moves = readInput("Day15_moves")
    part1(grid, moves).println()
    //part2(input).println()
}
