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

    fun moveCollectedCellsInColumn(
        cells: List<Cell<String>>,
        warehouse: Grid2D<String>,
        step: String
    ) {
        val sortedCells = if (step == "v") cells.sortedBy { it.y } else cells.sortedByDescending { it.y }
        val yOffset = if (step == "v") 1 else -1
        sortedCells.forEach { c ->
            warehouse.setCell(c.x, c.y + yOffset, c.value)
        }
        //println("sortedCells for x ${sortedCells.first().x}: $sortedCells")
        warehouse.setCell(sortedCells.first().x, sortedCells.first().y, ".")
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
        //println("For robot cell ${robotCell.x},${robotCell.y} found $cellsToMove to move")

        val columns = cellsToMove.groupBy { it.x }
        val yOffset = if (step == "v") 1 else -1

        val boundarySelector: (List<Cell<String>>) -> Int = if (step == "v") {
            { cells -> cells.maxOf { it.y } }
        } else {
            { cells -> cells.minOf { it.y } }
        }

        val canDoMove = columns.all { (x, cells) ->
            val targetY = boundarySelector(cells) + yOffset
            val cell = warehouse.getCell(x, targetY) // should never be null because of the borders
            cell.value == "."
        }
        //println("For robot cell ${robotCell.x},${robotCell.y} can do move: $canDoMove")

        // if all columns can move, then move all columns per column
        if (canDoMove) {
            columns.forEach { (_, cells) ->
                val cellsInColumn = warehouse.getCol(cells.first().x)
                    .filter { it.y in cells.minOf { it.y }..cells.maxOf { it.y } }
                moveCollectedCellsInColumn(cellsInColumn, warehouse, step)
            }
        }
    }

    tailrec fun doExpandedMoves(
        warehouse: Grid2D<String>,
        robotCell: Cell<String>,
        moves: List<String>
    ) {
        if (moves.isEmpty()) return
        val nrOfBoxesPre = Regex(Regex.escape("[]")).findAll(warehouse.toString()).count()
        val warehouseStringPre = warehouse.toString()
        when (moves.first()) {
            ">" -> singleMove(warehouse.getRow(robotCell.y), robotCell, warehouse)
            "<" -> singleMove(warehouse.getRow(robotCell.y).reversed(), robotCell, warehouse)
            "v" -> complexMove(robotCell, warehouse, "v")
            "^" -> complexMove(robotCell, warehouse, "^")
        }

        val nrOfBoxesPost = Regex(Regex.escape("[]")).findAll(warehouse.toString()).count()
        if(nrOfBoxesPre != nrOfBoxesPost) {
            println("Nr of boxes: $nrOfBoxesPre -> $nrOfBoxesPost")
            println("Warehouse before:")
            warehouseStringPre.println()
            println("Move: ${moves.first()}")
            warehouse.toString().println()
        }
        doExpandedMoves(warehouse, warehouse.getAllCells().first { it.value == "@" }, moves.drop(1))
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
        warehouse.toString().println()
        "".println()
        val moves = inputMoves.joinToString("").chunked(1)
        val robotCell = warehouse.getAllCells().first { it.value == "@" }
        doExpandedMoves(warehouse, robotCell, moves)
        val nrOfO = inputGrid.sumOf { it.count { c -> c == 'O' } }
        println("Nr of O: $nrOfO")
        return warehouse.getAllCells().filter { it.value == "[" }.sumOf { it.x + (it.y * 100L) }
    }

    val testGrid = readInput("Day15_test_grid")
    val testMoves = readInput("Day15_test_moves")
    // part1(testGrid, testMoves).println()
    part2(testGrid, testMoves).println()

    val grid = readInput("Day15_grid")
    val moves = readInput("Day15_moves")
    //part1(grid, moves).println()
    part2(grid, moves).println()
}
