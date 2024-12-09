data class Block(val len: Int, val id: Int) // -1 == empty

fun main() {

    fun calculate(list: List<Block>): Long {
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
            .withIndex()
            .filter { it.value != -1 }
            .fold(0L) { acc, (idx, block) -> acc + idx * block }
    }

    fun part1(input: List<String>): Long {
        val line = input.first()

        val list = line.mapIndexed { idx, char ->
            val digit = char.digitToInt()
            val id = if (idx % 2 == 1) -1 else idx / 2 // space
            Block(digit, id)
        }
        return calculate(list)
    }

    fun moveFiles(materialized: List<Int>): List<Int> {
        // Get unique file IDs in decreasing order, ignoring empty blocks (-1)
        val fileIds = materialized.filter { it != -1 }.distinct().sortedDescending()

        // Mutable copy of the initial state
        val currentState = materialized.toMutableList()

        // Process each file ID
        for (fileId in fileIds) {
            // Get current positions of the file
            val filePositions = currentState.withIndex().filter { it.value == fileId }.map { it.index }
            val fileLength = filePositions.size

            // Find the leftmost span of free blocks large enough to fit the file
            val targetStart = (0..currentState.size - fileLength).firstOrNull { start ->
                currentState.subList(start, start + fileLength).all { it == -1 }
            }

            if (targetStart != null) {
                // Clear the current positions of the file
                filePositions.forEach { currentState[it] = -1 }

                // Move the file to the target position
                repeat(fileLength) { i -> currentState[targetStart + i] = fileId }
            }
        }

        return currentState
    }


    fun part2(input: List<String>): Long {
        val line = input.first()

        val blocks = line.mapIndexed { idx, char ->
            val digit = char.digitToInt()
            val id = if (idx % 2 == 1) -1 else idx / 2 // space
            Block(digit, id)
        }

        val materialized = blocks.flatMap { block -> List(block.len) { block.id } }
        val finalState = moveFiles(materialized)

        finalState.joinToString("") { if (it == -1) "." else it.toString() }.println()

        return finalState
            .withIndex()
            .filter { it.value != -1 }
            .fold(0L) { acc, (idx, block) -> acc + idx * block }
    }

    val testInput = readInput("Day09_test")
    //part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day09")
    //part1(input).println()
    //part2(input).println()
}
