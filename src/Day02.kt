import kotlin.math.abs

fun main() {
    fun makeLevel(input: List<String>) = input.map { line ->
        line.split(" ")
            .filter { it !== " " && it !== "" }
            .map { it.toInt() }
    }

    fun isSafe(level: List<Int>): Boolean {
        val differences = level
            .zipWithNext()
            .map { it.first - it.second }
        return ((differences.all { it > 0 } || differences.all { it < 0 })
                && differences.all { abs(it) in 1..3 })
    }

    fun isSafeWithDampener(level: List<Int>): Boolean {
        val subLists = level.indices.map { level.take(it) + level.drop(it + 1) }
        return subLists.any { isSafe(it) }
    }

    fun part1(input: List<String>): Int {
        val levels = makeLevel(input)
        return levels.filter { level -> isSafe(level) }.size
    }

    fun part2(input: List<String>): Int {
        val levels = makeLevel(input)
        return levels.filter { level -> isSafeWithDampener(level) }.size
    }

    val testInput = readInput("Day02_test")
    //part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day02")
    //part1(input).println()
    part2(input).println()
}
