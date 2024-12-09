//data class DenseBlock(val id: Int, val fileLength: Int, val freeLength: Int?)
//data class ExpandedBlock(val blockId: String, val spaces: String)
//
//fun blockToString(denseBlock: DenseBlock): String {
//    val (id, fileLength, freeLength) = denseBlock
//    return id.toString().repeat(fileLength) + ".".repeat(freeLength ?: 0)
//}
//
//fun compactBlocks(input: List<DenseBlock>): List<DenseBlock> {
//    val blocks = input.toMutableList()
//    val freeSpaceQueue = mutableListOf<Pair<Int, Int>>() // Pair(blockIndex, freeLength)
//
//    // Collect free space details
//    blocks.forEachIndexed { index, block ->
//        if (block.freeLength != null && block.freeLength > 0) {
//            freeSpaceQueue.add(index to block.freeLength)
//        }
//    }
//
//    // Process each block's ID for compaction
//    for (i in blocks.indices) {
//        val currentBlock = blocks[i]
//        val idLength = currentBlock.fileLength
//        if (idLength == 0) continue
//
//        val idAsString = currentBlock.id.toString().repeat(idLength)
//        var remainingIdLength = idLength
//
//        // Attempt to fit the ID into free spaces
//        val newFreeSpaceQueue = mutableListOf<Pair<Int, Int>>()
//        for ((freeIndex, freeLength) in freeSpaceQueue) {
//            if (remainingIdLength == 0) {
//                newFreeSpaceQueue.add(freeIndex to freeLength) // Preserve unused free space
//                continue
//            }
//
//            val transferLength = minOf(remainingIdLength, freeLength)
//            val targetBlock = blocks[freeIndex]
//
//            // Update the target block
//            blocks[freeIndex] = targetBlock.copy(
//                fileLength = targetBlock.fileLength + transferLength,
//                freeLength = targetBlock.freeLength?.minus(transferLength)
//            )
//
//            // Update the remaining ID length
//            remainingIdLength -= transferLength
//
//            // Keep track of leftover free space
//            if (transferLength < freeLength) {
//                newFreeSpaceQueue.add(freeIndex to (freeLength - transferLength))
//            }
//        }
//
//        // Update the source block
//        blocks[i] = currentBlock.copy(
//            fileLength = remainingIdLength,
//            freeLength = currentBlock.freeLength
//        )
//
//        // Update the queue with remaining free space
//        freeSpaceQueue.clear()
//        freeSpaceQueue.addAll(newFreeSpaceQueue)
//    }
//
//    return blocks
//}
//
//fun main() {
//    fun part1(input: List<String>): Long {
//        val denseBlocks = input.first().chunked(2)
//            .mapIndexed { index, c ->
//                DenseBlock(
//                    index,
//                    c.substring(0, 1).toInt(),
//                    if (c.length == 2) c.substring(1, 2).toInt() else 0
//                )
//            }
//        denseBlocks.forEach { it.println() }
//        val exploded = denseBlocks.joinToString("") { blockToString(it) }
//        exploded.println()
//        val compactedBlocks = compactBlocks(denseBlocks)
//        val explodedCompact = compactedBlocks.joinToString("") { blockToString(it) }
//        explodedCompact.println()
//        return compactedBlocks.joinToString("") { blockToString(it) }.chunked(1)
//            .mapIndexed { index, s -> index.toLong() * (if (s != ".") s.toInt() else 0) }
//            .sumOf { it }
//    }
//
//    fun part2(input: List<String>): Int {
//        return input.size
//    }
//
//    val testInput = readInput("Day09_test")
//    part1(testInput).println()
//    //part2(testInput).println()
//
//    val input = readInput("Day09")
//    //part1(input).println()
//    //part2(input).println()
//}
