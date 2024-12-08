fun main() {
    fun makeGrid(input: List<String>): Grid2D<String> =
        Grid2D(input.filterNot(String::isEmpty).map { it.chunked(1) })

    fun getPairNodes(
        grid: Grid2D<String>,
        pair: Pair<Cell<String>, Cell<String>>,
        multiplier: Int
    ): List<Cell<String>> {
        val (start, end) = pair
        val dx = end.x - start.x
        val dy = end.y - start.y
        return listOfNotNull(
            grid.getCellOrNull(start.x - multiplier * dx, start.y - multiplier * dy),
            grid.getCellOrNull(end.x + multiplier * dx, end.y + multiplier * dy)
        )
    }

    fun getPairAntinodes(grid: Grid2D<String>, pair: Pair<Cell<String>, Cell<String>>): List<Cell<String>> =
        getPairNodes(grid, pair, 1) // Use multiplier = 1 for antinodes

    fun getPairHarmonics(grid: Grid2D<String>, pair: Pair<Cell<String>, Cell<String>>): List<Cell<String>> =
        generateSequence(1) { it + 1 }
            .map { multiplier -> getPairNodes(grid, pair, multiplier) }
            .takeWhile { it.isNotEmpty() }
            .flatten()
            .toList()

    fun getSignalAntinodes(grid: Grid2D<String>, signal: String): List<Cell<String>> =
        grid.getAllCells()
            .filter { it.value == signal }
            .uniquePairs()
            .flatMap { getPairAntinodes(grid, it) }

    fun getSignalHarmonics(grid: Grid2D<String>, signal: String): List<Cell<String>> {
        val signalCells = grid.getAllCells().filter { it.value == signal }
        return signalCells
            .uniquePairs()
            .flatMap { getPairHarmonics(grid, it) } + signalCells // Add original signal cells as they are also part of the harmonics
    }

    fun part1(input: List<String>): Int {
        val grid = makeGrid(input)
        return grid.getAllCells()
            .map { it.value }
            .filterNot { it == "." }
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 } // Keep signals that occur more than once
            .keys
            .flatMap { getSignalAntinodes(grid, it) }
            .distinct()
            .size
    }

    fun part2(input: List<String>): Int {
        val grid = makeGrid(input)
        return grid.getAllCells()
            .map { it.value }
            .filterNot { it == "." }
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 } // Keep signals that occur more than once
            .keys
            .flatMap { getSignalHarmonics(grid, it) }
            .distinct()
            .size
    }

    val testInput = readInput("Day08_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
