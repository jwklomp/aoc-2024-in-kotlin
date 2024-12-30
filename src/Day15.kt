fun main() {
    fun makeGrid(input: List<String>): Grid2D<String> =
        Grid2D(input.filterNot(String::isEmpty).map { it.chunked(1).toMutableList() })

    fun makeExpandedGrid(input: List<String>): Grid2D<String> =
        Grid2D(
            input.filterNot(String::isEmpty)
                .map {
                    it.chunked(1).flatMap { c ->
                        when (c) {
                            "#" -> listOf("#", "#")
                            "O" -> listOf("[", "]")
                            "@" -> listOf("@", ".")
                            "." -> listOf(".", ".")
                            else -> emptyList()
                        }
                    }.toMutableList()
                })

    fun singleMove(
        cells: List<Cell<String>>,
        startCell: Cell<String>,
        warehouse: Grid2D<String>
    ) {
        val involvedCells = cells.dropWhile { it != startCell }.takeWhile { it.value != "#" }
        if (involvedCells.map { it.value }.contains(".")) { // can only move if there is space before the wall
            val updateCells = involvedCells.takeWhileInclusive { it.value != "." }
            updateCells.forEachIndexed { i, c ->
                if (i == 0) warehouse.setCell(c.x, c.y, ".")
                else warehouse.setCell(c.x, c.y, updateCells[i - 1].value)
            }
        }
    }

    fun moveCellsByColumn(
        cells: List<Cell<String>>,
        warehouse: Grid2D<String>,
        yOffset: Int
    ) {
        cells.forEach { c -> warehouse.setCell(c.x, c.y + yOffset, c.value) }
        val oldYs = cells.map { it.y }
        val newYs = cells.map { it.y + yOffset }
        val yToClear = oldYs.filterNot { it in newYs }
        yToClear.forEach { y ->
            warehouse.setCell(cells.first().x, y, ".")
        }
    }

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
        singleMove(cells, robotCell, warehouse)
        doMoves(warehouse, warehouse.getAllCells().first { it.value == "@" }, moves.drop(1))
    }

    fun nextLeveBoxCells(current: Cell<String>, warehouse: Grid2D<String>, step: String): List<Cell<String>> {
        val yOffset = if (step == "v") 1 else -1
        val nextCell = warehouse.getCell(current.x, current.y + yOffset)
        return when (nextCell.value) {
            "[" -> listOf(nextCell, warehouse.getCell(current.x + 1, current.y + yOffset))
            "]" -> listOf(nextCell, warehouse.getCell(current.x - 1, current.y + yOffset))
            else -> emptyList()
        }
    }

    tailrec fun collectCellsToMove(
        warehouse: Grid2D<String>,
        step: String,
        currentLevelCells: List<Cell<String>>,
        collectedCells: MutableList<Cell<String>>
    ): List<Cell<String>> {
        val nextLevelBoxCells = currentLevelCells.flatMap { nextLeveBoxCells(it, warehouse, step) }
        if (nextLevelBoxCells.isEmpty()) return collectedCells
        collectedCells.addAll(nextLevelBoxCells)
        return collectCellsToMove(warehouse, step, nextLevelBoxCells, collectedCells)
    }

    fun complexMove(robotCell: Cell<String>, warehouse: Grid2D<String>, step: String) {
        val cellsToMove = collectCellsToMove(warehouse, step, listOf(robotCell), mutableListOf(robotCell)).distinct()
        val columns = cellsToMove.groupBy { it.x }
        val yOffset = if (step == "v") 1 else -1

        val canDoMove = columns.all { (_, cells) ->
            val nextCells = cells.map { warehouse.getCell(it.x, it.y + yOffset) }
            nextCells.none { it.value == "#" }
        }
        if (canDoMove) {
            columns.forEach { (_, cells) ->
                val sorted = if (step == "v") cells.sortedBy { it.y } else cells.sortedByDescending { it.y }
                moveCellsByColumn(sorted, warehouse, yOffset)
            }
        }
    }

    tailrec fun doExpandedGridMoves(
        warehouse: Grid2D<String>,
        robotCell: Cell<String>,
        moves: List<String>
    ) {
        if (moves.isEmpty()) return
        when (moves.first()) {
            ">" -> singleMove(warehouse.getRow(robotCell.y), robotCell, warehouse)
            "<" -> singleMove(warehouse.getRow(robotCell.y).reversed(), robotCell, warehouse)
            "v" -> complexMove(robotCell, warehouse, "v")
            "^" -> complexMove(robotCell, warehouse, "^")
        }
        doExpandedGridMoves(warehouse, warehouse.getAllCells().first { it.value == "@" }, moves.drop(1))
    }

    fun part1(inputGrid: List<String>, inputMoves: List<String>): Long {
        val warehouse = makeGrid(inputGrid)
        val moves = inputMoves.joinToString("").chunked(1)
        val robotCell = warehouse.getAllCells().first { it.value == "@" }
        doMoves(warehouse, robotCell, moves)
        return warehouse.getAllCells().filter { it.value == "O" }.sumOf { it.x + (it.y * 100L) }
    }

    fun part2(inputGrid: List<String>, inputMoves: List<String>): Long {
        val warehouse = makeExpandedGrid(inputGrid)
        val moves = inputMoves.joinToString("").chunked(1)
        val robotCell = warehouse.getAllCells().first { it.value == "@" }
        doExpandedGridMoves(warehouse, robotCell, moves)
        return warehouse.getAllCells().filter { it.value == "[" }.sumOf { it.x + (it.y * 100L) }
    }

    val testGrid = readInput("Day15_test_grid")
    val testMoves = readInput("Day15_test_moves")
    part1(testGrid, testMoves).println()
    part2(testGrid, testMoves).println()

    val grid = readInput("Day15_grid")
    val moves = readInput("Day15_moves")
    part1(grid, moves).println()
    part2(grid, moves).println()
}
