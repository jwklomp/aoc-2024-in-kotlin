fun main() {
    fun splitStones(stones: MutableList<Long>) {
        val iterator = stones.listIterator()
        while (iterator.hasNext()) {
            val stone = iterator.next()
            when {
                stone == 0L -> iterator.set(1)
                stone.toString().length % 2 == 0 -> {
                    val stoneString = stone.toString()
                    val newElements = stoneString.chunked(stoneString.length / 2).map { it.toLong() }
                    iterator.remove()
                    newElements.forEach { iterator.add(it) }
                }

                else -> iterator.set(stone * 2024)
            }
        }
    }

    // For part 2 splitStones is too slow, so we use a map instead of a list
    fun splitStonesWithMap(stoneCounts: Map<Long, Long>): Map<Long, Long> {
        val newStoneCounts = mutableMapOf<Long, Long>()

        for ((stone, count) in stoneCounts) {
            when {
                stone == 0L -> {
                    newStoneCounts[1] = newStoneCounts.getOrDefault(1, 0L) + count
                }

                stone.toString().length % 2 == 0 -> {
                    val stoneString = stone.toString()
                    val newElements = stoneString.chunked(stoneString.length / 2).map { it.toLong() }
                    for (newStone in newElements) {
                        newStoneCounts[newStone] = newStoneCounts.getOrDefault(newStone, 0L) + count
                    }
                }

                else -> {
                    val newStone = stone * 2024
                    newStoneCounts[newStone] = newStoneCounts.getOrDefault(newStone, 0L) + count
                }
            }
        }
        return newStoneCounts
    }

    fun part1(input: List<String>): Long {
        val times = 25
        val stones = input.first().split(" ").map { it.toLong() }.toMutableList()
        (0..< times).forEach {
            splitStones(stones)
            println("iteration ${it}: nr of stones: ${stones.size}")
        }
        return stones.size.toLong()
    }

    fun part2(input: List<String>): Long {
        val times = 75
        val stones = input.first().split(" ").map { it.toLong() }
        var stoneCountsMap = stones.groupingBy { it }.eachCount().mapValues { it.value.toLong() }

        repeat(times) { iteration ->
            stoneCountsMap = splitStonesWithMap(stoneCountsMap)
            val totalStones = stoneCountsMap.values.sum()
            println("Iteration $iteration: Total stones: $totalStones")
        }

        return stoneCountsMap.values.sum()
    }

    val testInput = readInput("Day11_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}
