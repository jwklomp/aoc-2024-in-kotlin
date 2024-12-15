fun main() {
    fun makeGridBordered(input: List<String>): Grid2D<Bordered<String>> =
        Grid2D(input.map { s -> s.chunked(1).map { Bordered(id = it) } })

    fun part1(input: List<String>): Int {
        val borderedGrid = makeGridBordered(input)
        val regionData = getRegionsWithData(borderedGrid)
        return regionData.sumOf { it.area * it.perimeter }
    }

    fun part2(input: List<String>): Int {
        val borderedGrid = makeGridBordered(input)
        val regionData = getRegionsWithData(borderedGrid)
        return regionData.sumOf { it.area * it.sides }
    }

    val testInput = readInput("Day12_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
