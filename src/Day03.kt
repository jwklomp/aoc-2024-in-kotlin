fun removeDeactivatedPortions(input: String): String {
    var skip = false
    var index = 0

    return buildString {
        while (index < input.length) {
            when {
                input.startsWith("don't()", index) -> {
                    skip = true
                    index += "don't()".length
                }
                input.startsWith("do()", index) -> {
                    skip = false
                    index += "do()".length
                }
                skip -> {
                    index++
                }
                else -> {
                    append(input[index])
                    index++
                }
            }
        }
    }
}

fun extractMulMatches(input: String): List<Pair<Long, Long>> {
    val regex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
    return regex.findAll(input).map { matchResult ->
        val x = matchResult.groupValues[1].toLong()
        val y = matchResult.groupValues[2].toLong()
        x to y
    }.toList()
}

fun main() {

    fun part1(input: List<String>): Long {
        return input.sumOf { extractMulMatches(it).sumOf { (x, y) -> (x * y) } }
    }

    fun part2(input: List<String>): Long {
        val sanitized = removeDeactivatedPortions(input.joinToString())
        return extractMulMatches(sanitized).sumOf { (x, y) -> (x * y) }
    }

    val testInput = readInput("Day03_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
