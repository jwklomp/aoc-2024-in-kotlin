fun main() {
    fun toEquationPair(s: String): Pair<Long, List<Long>> {
        val (a, b) = s.split(": ")
        return a.toLong() to b.split(" ").map { it.toLong() }
    }

    fun canMatchEquation(sum: Long, numbers: List<Long>, operators: List<String>): Boolean {
        fun evaluateLeftToRight(values: List<Long>, ops: List<String>): Long =
            ops.foldIndexed(values.first()) { index, acc, op ->
                when (op) {
                    "+" -> acc + values[index + 1]
                    "*" -> acc * values[index + 1]
                    "||" -> (acc.toString() + values[index + 1].toString()).toLong()
                    else -> throw IllegalArgumentException("Unsupported operator")
                }
            }

        fun generateOperatorCombinations(size: Int): List<List<String>> =
            (1..size).fold(listOf(emptyList<String>())) { acc, _ ->
                acc.flatMap { combo -> operators.map { combo + it } }
            }

        val operatorCombinations = generateOperatorCombinations(numbers.size - 1)
        return operatorCombinations.any { ops ->
            evaluateLeftToRight(numbers, ops) == sum
        }
    }

    fun part1(input: List<String>): Long =
        input.map { toEquationPair(it) }.filter { (sum, numbers) ->
            canMatchEquation(sum, numbers, listOf("+", "*"))
        }.sumOf { it.first }

    fun part2(input: List<String>): Long =
        input.map { toEquationPair(it) }.filter { (sum, numbers) ->
            canMatchEquation(sum, numbers, listOf("+", "*", "||"))
        }.sumOf { it.first }

    val testInput = readInput("Day07_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
