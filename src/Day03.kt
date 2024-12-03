fun main() {
    fun extractMulMatches(input: String): List<Pair<Int, Int>> {
        val regex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
        return regex.findAll(input).map { matchResult ->
            val x = matchResult.groupValues[1].toInt()
            val y = matchResult.groupValues[2].toInt()
            x to y
        }.toList()
    }


    fun part1(input: List<String>): Long {
        input.println()
        return extractMulMatches(input[0]).sumOf { (x, y) -> (x * y).toLong() }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readInput("Day03_test")
    part1(testInput).println()
    //part2(testInput).println()

    val input = readInput("Day03")
    part1(input).println()
    //part2(input).println()
}
