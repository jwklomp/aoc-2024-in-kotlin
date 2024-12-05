// TODO refactor and move to extension functions
fun getPrimaryDiagonals(grid: List<String>): List<String> {
    val rows = grid.size
    val cols = grid.firstOrNull()?.length ?: 0
    val diagonals = mutableListOf<String>()

    // Diagonals starting from the first row
    for (startCol in 0 until cols) {
        var row = 0
        var col = startCol
        val diagonal = StringBuilder()

        while (row < rows && col < cols) {
            diagonal.append(grid[row][col])
            row++
            col++
        }
        diagonals.add(diagonal.toString())
    }

    // Diagonals starting from the first column (excluding the first element)
    for (startRow in 1 until rows) {
        var row = startRow
        var col = 0
        val diagonal = StringBuilder()

        while (row < rows && col < cols) {
            diagonal.append(grid[row][col])
            row++
            col++
        }
        diagonals.add(diagonal.toString())
    }
    return diagonals
}

fun getSecondaryDiagonals(grid: List<String>): List<String> {
    val rows = grid.size
    val cols = grid.firstOrNull()?.length ?: 0
    val diagonals = mutableListOf<String>()

    // Diagonals starting from the last column
    for (startCol in cols - 1 downTo 0) {
        var row = 0
        var col = startCol
        val diagonal = StringBuilder()

        while (row < rows && col >= 0) {
            diagonal.append(grid[row][col])
            row++
            col--
        }
        diagonals.add(diagonal.toString())
    }

    // Diagonals starting from the first column (excluding the last element)
    for (startRow in 1 until rows) {
        var row = startRow
        var col = cols - 1
        val diagonal = StringBuilder()

        while (row < rows && col >= 0) {
            diagonal.append(grid[row][col])
            row++
            col--
        }
        diagonals.add(diagonal.toString())
    }
    return diagonals
}

fun wordCounter(grid: List<String>, word: String): Int {
    val searchList = listOf(word, word.reversed())
    // Rows
    val rowCount = grid.sumOf { row -> row.windowed(4).filter { part -> searchList.contains(part) }.size }

    // Columns
    val transposed = transpose(grid.map { it.split("")}).map { it.joinToString("") }
    val columnCount = transposed.sumOf { row -> row.windowed(4).filter { part -> searchList.contains(part) }.size }

    // Primary diagonals
    val primaryDiagonals = getPrimaryDiagonals(grid)
    val primaryCount = primaryDiagonals.sumOf { row -> row.windowed(4).filter { part -> searchList.contains(part) }.size }

    // Secondary diagonals
    val secondaryDiagonals = getSecondaryDiagonals(grid)
    val secondaryCount = secondaryDiagonals.sumOf { row -> row.windowed(4).filter { part -> searchList.contains(part) }.size }

    return rowCount + columnCount + primaryCount + secondaryCount
}

fun isMasInAnX(window: List<List<String>>, searchList: List<String>): Boolean {
    val primaryDiagonal = window.mapIndexed { i, row -> row[i] }.joinToString("")
    val secondaryDiagonal = window.mapIndexed { i, row -> row[window.size - 1 - i] }.joinToString("")
    return searchList.contains(primaryDiagonal) && searchList.contains(secondaryDiagonal)
}

fun xCounter(grid: List<String>, word: String): Int {
    val searchList = listOf(word, word.reversed())
    val windows = grid.map { it.split("")}.windowed2D(3)
    return windows.filter { isMasInAnX(it, searchList) }.size
}

fun main() {
    fun part1(input: List<String>): Int {
        val searchWord = "XMAS"
        return wordCounter(input, searchWord)
    }

    fun part2(input: List<String>): Int {
        val searchWord = "MAS"
        return xCounter(input, searchWord)
    }

    val testInput = readInput("Day04_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
