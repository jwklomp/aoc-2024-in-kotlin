fun main() {
    fun part1(input: List<String>): Int {
        input.println()
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readInput("Day10_test")
    part1(testInput).println()
    //part2(testInput).println()

    val input = readInput("Day10")
    //part1(input).println()
    //part2(input).println()
}
