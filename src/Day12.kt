fun main() {
    fun makeGrid(input: List<String>): Grid2D<String> =
        Grid2D(input.map { it.chunked(1) })

    fun part1(input: List<String>): Int {
        val gardenGrid = makeGrid(input)
        val regionData = getRegionsWithData(gardenGrid)
        regionData.forEach { println(it) }
        return regionData.sumOf { it.second * it.third }
    }

    fun part2(input: List<String>): Int {
        val gardenGrid = makeGrid(input)
        val regionData = getRegionsWithData(gardenGrid)
        regionData.forEach { println(it) }
        return regionData.sumOf { it.second * it.fourth }
    }

    val testInput = readInput("Day12_test")
    //part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day12")
    //part1(input).println()
    //part2(input).println()
}
