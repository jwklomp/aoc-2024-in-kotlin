import kotlin.math.floor

fun isSortedCorrectly(rules: Map<Int, List<Int>>, line: List<Int>): Boolean {
    // for all elements in the line, check all map values are NOT all items before the key
    return line.all { pageId ->
        val mustBeAfterRuleElements = rules[pageId].orEmpty()
        val beforeListElements = line.takeWhile{ it != pageId }
        beforeListElements.none { it in mustBeAfterRuleElements }
    }
}

fun main() {
    fun part1(rulesInput: List<String>, linesInput: List<String>): Int {

        val rules = rulesInput.map { it.split("|").map { it.toInt() } }
            .sortedBy { it.first()}
            .groupBy { it[0] }
            .map { it.key to it.value.map { it[1] } }
            .toMap()

        val lines = linesInput.map { it.split(",").map { it.toInt() } }
        val correctLines = lines.filter { line -> isSortedCorrectly(rules, line) }
        return correctLines.sumOf { line -> line[floor((line.size / 2).toDouble()).toInt()] }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testRulesInput = readInput("Day05_rules_test")
    val testLinesInput = readInput("Day05_lines_test")
    part1(testRulesInput, testLinesInput).println()
    //part2(testInput).println()

    val rulesInput = readInput("Day05_rules")
    val linesInput = readInput("Day05_lines")
    part1(rulesInput, linesInput).println()
    //part2(input).println()
}
