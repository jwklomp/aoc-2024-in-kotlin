import kotlin.math.floor

fun makeRulesMap(rulesInput: List<String>): Map<Int, List<Int>> {
    val rules = rulesInput.map { it.split("|").map { it.toInt() } }
        .sortedBy { it.first() }
        .groupBy { it[0] }
        .map { it.key to it.value.map { it[1] } }
        .toMap()
    return rules
}

fun isSortedCorrectly(rules: Map<Int, List<Int>>, line: List<Int>): Boolean {
    // for all elements in the line, check all map values are NOT all items before the key
    return line.all { pageId ->
        val mustBeAfterRuleElements = rules[pageId].orEmpty()
        val beforeListElements = line.takeWhile { it != pageId }
        beforeListElements.none { it in mustBeAfterRuleElements }
    }
}

// Topological sort
// TODO: Refactor and put in utils.
fun buildTopologicalSortOrderFromList(dependencyPairs: List<List<Int>>): List<Int> {
    val inDegree = mutableMapOf<Int, Int>()  // To track in-degree of nodes
    val adjacencyList = mutableMapOf<Int, MutableList<Int>>()  // Adjacency list representation

    dependencyPairs.forEach { pair ->
        val (node, dependency) = pair
        adjacencyList.computeIfAbsent(node) { mutableListOf() }.add(dependency)
        inDegree[node] = inDegree.getOrDefault(node, 0)
        inDegree[dependency] = inDegree.getOrDefault(dependency, 0) + 1
    }

    val queue = ArrayDeque<Int>()
    inDegree.filterValues { it == 0 }.keys.forEach { queue.add(it) }
    val topologicalOrder = mutableListOf<Int>()

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        topologicalOrder.add(current)

        adjacencyList[current]?.forEach { neighbor ->
            inDegree[neighbor] = inDegree[neighbor]!! - 1
            if (inDegree[neighbor] == 0) {
                queue.add(neighbor)
            }
        }
    }

    // Throw exception if there is a cycle or disconnected graph
    if (topologicalOrder.size != inDegree.size) {
        throw IllegalArgumentException("Cycle detected or disconnected graph")
    }

    return topologicalOrder
}

/**
 * A part of the description that turned out to be important:
 * "Because the first update does not include some page numbers, the ordering rules involving those missing page numbers are ignored."
 *
 * So for each line, we need to contruct the topological order based on the rules that have numbers matching the line...
 * IF you don't do this, you will get a cycle in the topological order.
 */
fun sortTopologicalOrder(line: List<Int>, rulesInput: List<String>): List<Int> {
    val rules = rulesInput.map { it.split("|").map { it.toInt() } }
        .filter { (from, to) -> from in line && to in line }
    val topologicalOrder = buildTopologicalSortOrderFromList(rules)
    return sortListByTopologicalOrder(line, topologicalOrder)
}

fun sortListByTopologicalOrder(list: List<Int>, topologicalOrder: List<Int>): List<Int> {
    val orderIndexMap = topologicalOrder.withIndex().associate { it.value to it.index }
    return list.sortedBy { orderIndexMap[it] ?: Int.MAX_VALUE } // Unknown values go to the end
}

fun main() {
    fun part1(rulesInput: List<String>, linesInput: List<String>): Int {
        val rules = makeRulesMap(rulesInput)
        val lines = linesInput.map { it.split(",").map { it.toInt() } }
        val correctLines = lines.filter { line -> isSortedCorrectly(rules, line) }
        return correctLines.sumOf { line -> line[floor((line.size / 2).toDouble()).toInt()] }
    }

    fun part2(rulesInput: List<String>, linesInput: List<String>): Int {
        val lines = linesInput.map { it.split(",").map { it.toInt() } }
        val rules = makeRulesMap(rulesInput)
        val incorrectLines = lines.filter { line -> !isSortedCorrectly(rules, line) }
        val sortedIncorrectLines = incorrectLines.map { line -> sortTopologicalOrder(line, rulesInput) }
        return sortedIncorrectLines.sumOf { line -> line[floor((line.size / 2).toDouble()).toInt()] }
    }

    val testRulesInput = readInput("Day05_rules_test")
    val testLinesInput = readInput("Day05_lines_test")
    part1(testRulesInput, testLinesInput).println()
    part2(testRulesInput, testLinesInput).println()

    val rulesInput = readInput("Day05_rules")
    val linesInput = readInput("Day05_lines")
    part1(rulesInput, linesInput).println()
    part2(rulesInput, linesInput).println()
}
