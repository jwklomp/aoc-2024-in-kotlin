data class Block(val len: Int, val id: Int) // -1 == empty

fun main() {
    fun moveBlocks(list: List<Block>): List<Int> {
        val expanded = list.flatMap { block -> List(block.len) { block.id } }
        val redistributed = generateSequence(expanded) { current ->
            val first = current.indexOfFirst { it == -1 }
            val last = current.indexOfLast { it != -1 }

            if (first < last) {
                current.toMutableList().also {
                    it[first] = current[last]
                    it[last] = -1
                }
            } else {
                null
            }
        }.last()
        return redistributed
    }

    fun moveFiles(blocks: List<Block>): List<Int> {
        val expanded = blocks.flatMap { block -> List(block.len) { block.id } }
        // Get unique file IDs in decreasing order, ignoring empty blocks (-1)
        val fileIds = expanded.filter { it != -1 }.distinct().sortedDescending()
        val currentState = expanded.toMutableList()

        // Process each file ID
        for (fileId in fileIds) {
            // Get current positions of the file
            val filePositions = currentState.withIndex().filter { it.value == fileId }.map { it.index }
            val fileLength = filePositions.size

            // Find the left most span of free blocks large enough to fit the file
            val targetStart = (0..currentState.size - fileLength).firstOrNull { start ->
                currentState.subList(start, start + fileLength).all { it == -1 }
            }
            // only move if there is target start and target start is smaller than the current position
            if (targetStart != null && targetStart < filePositions.minOrNull()!!) {
                // Clear the current positions of the file
                filePositions.forEach { currentState[it] = -1 }
                repeat(fileLength) { i -> currentState[targetStart + i] = fileId }
            }
        }

        return currentState
    }

    fun makeBlockList(input: List<String>): List<Block> =
        input.first().mapIndexed { idx, char ->
            val digit = char.digitToInt()
            val id = if (idx % 2 == 1) -1 else idx / 2 // space
            Block(digit, id)
        }

    fun part1(input: List<String>): Long {
        val list = makeBlockList(input)
        return moveBlocks(list)
            .withIndex()
            .filter { it.value != -1 }
            .fold(0L) { acc, (idx, block) -> acc + idx * block }
    }

    fun part2(input: List<String>): Long {
        val blocks = makeBlockList(input)
        val finalState = moveFiles(blocks)
        return finalState
            .withIndex()
            .filter { it.value != -1 }
            .fold(0L) { acc, (idx, block) -> acc + idx * block }
    }

    val testInput = readInput("Day09_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
