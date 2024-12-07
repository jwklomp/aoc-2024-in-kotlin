fun main() {
    fun toEquationPair(s: String): Pair<Long, List<Long>> {
        val (a, b) = s.split(": ")
        return a.toLong() to b.split(" ").map { it.toLong() }
    }

    val operators = listOf("+", "*")

    fun canMatchEquation(sum: Long, numbers: List<Long>, operators: List<String>): Boolean {
        fun evaluateLeftToRight(values: List<Long>, ops: List<String>): Long =
            ops.foldIndexed(values.first()) { index, acc, op ->
                when (op) {
                    "+" -> acc + values[index + 1]
                    "*" -> acc * values[index + 1]
                    else -> throw IllegalArgumentException("Unsupported operator")
                }
            }

        // Generate all possible combinations of operators for the given number of numbers - 1
        fun generateOperatorCombinations(size: Int): List<List<String>> {
            if (size == 0) return listOf(emptyList())
            val smallerCombos = generateOperatorCombinations(size - 1)
            return smallerCombos.flatMap { combo -> operators.map { combo + it } }
        }

        // Check all combinations
        val operatorCombinations = generateOperatorCombinations(numbers.size - 1)
        return operatorCombinations.any { ops ->
            evaluateLeftToRight(numbers, ops) == sum
        }
    }

    fun part1(input: List<String>): Long {
        return input.map { toEquationPair(it) }.filter { (sum, numbers) ->
            canMatchEquation(sum, numbers, operators)
        }.sumOf { it.first }

    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    val testInput = readInput("Day07_test")
    part1(testInput).println()
    //part2(testInput).prLongln()

    val input = readInput("Day07")
    part1(input).println()
    //part2(input).prLongln()
}
