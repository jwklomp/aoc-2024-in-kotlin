//data class DenseBlock(
//    val id: Int,
//    val idString: String,
//    var fileLength: Int,
//    var freeLength: Int?,
//    var replacementVal: String
//)
//
//data class ExpandedBlock(val blockId: String, val spaces: String)
//
//fun blockToString(denseBlock: DenseBlock): String {
//    val (_, idString, fileLength, _, replacementVal) = denseBlock
//    return idString.repeat(fileLength) + replacementVal
//}
//
//fun compactBlocks(blocks: List<DenseBlock>): List<DenseBlock> {
//    while (true) {
//        // get the last block with a file length > 0
//        val lastId = blocks.indexOfLast { it.fileLength > 0 }
//        val stringToMove = blocks[lastId].idString
//
//        // get the first block with a free space >= stringToMove.length
//        val firstId = blocks.indexOfFirst { (it.freeLength ?: 0) >= stringToMove.length }
//
//        if (firstId == -1 || firstId >= lastId) {
//            break // No valid transformation possible
//        }
//
//        val firstBlock = blocks[firstId]
//        // update replacementVal: replace the first X dots with the idString where X is the length of the idString
//        firstBlock.replacementVal =
//            firstBlock.replacementVal.replaceFirst(".".repeat(stringToMove.length), stringToMove)
//        firstBlock.freeLength = (firstBlock.freeLength ?: 0) - stringToMove.length
//
//        val lastBlock = blocks[lastId]
//        lastBlock.replacementVal += ".".repeat(stringToMove.length)
//        --lastBlock.fileLength
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
//                    id = index,
//                    idString = index.toString(),
//                    fileLength = c.substring(0, 1).toInt(),
//                    freeLength = if (c.length == 2) c.substring(1, 2).toInt() else 0,
//                    replacementVal = ".".repeat(if (c.length == 2) c.substring(1, 2).toInt() else 0)
//                )
//            }.sortedBy { it.id }
//        val compactedBlocks = compactBlocks(denseBlocks)
//        compactedBlocks.forEach { it.println() }
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
//    part1(input).println()
//    //part2(input).println()
//}
