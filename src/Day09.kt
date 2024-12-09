data class Block(val id: Int, val fileLength: Int, val freeLength: Int?)

// TODO '99' can count is one block so need 2 spaces. Need to fix this
fun blockToString(block: Block): String {
    val (id, fileLength, freeLength) = block
    return id.toString().repeat(fileLength) + ".".repeat(freeLength ?: 0)
}

fun compactString(input: String): String {
    val builder = StringBuilder(input)

    while (true) {
        val firstDotIndex = builder.indexOf(".")
        val lastNonDotIndex = builder.indexOfLast { it != '.' }

        if (firstDotIndex == -1 || lastNonDotIndex == -1 || firstDotIndex >= lastNonDotIndex) {
            break // No valid transformation possible
        }

        // Replace the first dot with the last non-dot character
        val lastNonDotChar = builder[lastNonDotIndex]
        builder.setCharAt(firstDotIndex, lastNonDotChar)

        // Remove the last non-dot character
        builder.deleteCharAt(lastNonDotIndex)
    }

    return builder.toString()
}

fun main() {
    fun part1(input: List<String>): Long {
        val blocks = input.first().chunked(2)
            .mapIndexed { index, c ->
                Block(
                    index,
                    c.substring(0, 1).toInt(),
                    if (c.length == 2) c.substring(1, 2).toInt() else 0
                )
            }
        blocks.takeLast(3).forEach { it.println() }
        val exploded = blocks.joinToString("") { blockToString(it) }
        exploded.length.println()
        exploded.replace(".", "").length.println()
        val compacted = compactString(exploded).replace(".", "")
        compacted.length.println()

        return compacted.chunked(1)
            .mapIndexed { index, s -> index.toLong() * s.toInt() }
            .sumOf { it }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readInput("Day09_test")
    part1(testInput).println()
    //part2(testInput).println()

    val input = readInput("Day09")
    part1(input).println()
    //part2(input).println()
}
