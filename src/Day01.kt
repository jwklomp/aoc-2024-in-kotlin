import kotlin.math.abs

fun main() {
    fun makePairs(input: List<String>) = input.map { line ->
        line.split(" ")
            .filter { it !== " " && it !== "" }
            .map { it.toInt() }
    }

    fun part1(input: List<String>): Int {
        val pairs = makePairs(input)
        val firstList = pairs.map { it.first() }.sorted()
        val secondList = pairs.map { it.last() }.sorted()
        return firstList.zip(secondList).sumOf { abs(it.first - it.second) }
    }

    fun part2(input: List<String>): Int {
        val pairs = makePairs(input)
        val firstList = pairs.map { it.first() }
        val secondList = pairs.map { it.last() }
        return firstList.sumOf { first ->
            first * (secondList.count { it == first })
        }
    }

    val testInput = readInput("Day01_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
