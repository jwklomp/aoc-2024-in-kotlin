fun main() {
    data class Machine(val buttonA: Pair<Int, Int>, val buttonB: Pair<Int, Int>, val prize: Pair<Int, Int>)

    val buttonRegex = Regex("""^Button [AB]: X\+(\d+), Y\+(\d+)""")
    val prizeRegex = Regex("""^Prize: X=(\d+), Y=(\d+)""")

    fun extractValues(regex: Regex, input: String): Pair<Int, Int> {
        val matchResult = regex.find(input)
        return matchResult!!.let {
            val (x, y) = it.destructured
            Pair(x.toInt(), y.toInt())
        }
    }

    // get all combinations of x and y that make up the total with a limit on the number of a and b
    // returns a list of pairs of x and y
    fun getCombinations(total: Int, a: Int, b: Int, limit: Int): List<Pair<Int, Int>> {
        return (0..limit).flatMap { i ->
            (0..limit).map { j -> Pair(i, j) }
        }.filter { it.first * a + it.second * b == total }
    }

    fun makeMachines(input: List<String>): List<Machine> {
        val inputBlocks = splitOnEmptyLine(input)
        val machines = inputBlocks.map { block ->
            val buttonA = extractValues(buttonRegex, block.first())
            val buttonB = extractValues(buttonRegex, block[1])
            val prize = extractValues(prizeRegex, block[2])
            Machine(buttonA, buttonB, prize)
        }
        return machines
    }

    // Turned out to be a linear equation problem. Using Cramer's Rule to solve the equations.
    // See for example https://www.youtube.com/watch?v=vXqlIOX2itM
    // Also turned out to be touch of trickiness in the question, there are never multiple solutions, only one or none.
    // We must keep in mind that we only want exact numeric solutions, so we must check that the numerators are divisible by the determinant.
    fun solveLinearEquations(
        a: Pair<Int, Int>,
        b: Pair<Int, Int>,
        xy: Pair<Long, Long>
    ): Pair<Long, Long>? {
        val (ax, ay) = a
        val (bx, by) = b
        val (px, py) = xy
        val ca = (px * by - py * bx).toDouble() / (ax * by - ay * bx).toDouble()
        val cb = (px - ax * ca) / bx.toDouble()
        return if(ca % 1 == 0.toDouble() && cb % 1 == 0.toDouble()) {
            Pair(ca.toLong(), cb.toLong())
        } else {
            null
        }
    }


    fun part1(input: List<String>): Int {
        val machines = makeMachines(input)
        val validMachineResults = machines.mapNotNull { machine ->
            val xCombinations = getCombinations(machine.prize.first, machine.buttonA.first, machine.buttonB.first, 100)
            val yCombinations =
                getCombinations(machine.prize.second, machine.buttonA.second, machine.buttonB.second, 100)
            val matches = xCombinations.filter { x -> yCombinations.any { y -> x == y } }
            matches.minOfOrNull { it.first * 3 + it.second }
        }
        return validMachineResults.sum()
    }

    fun part2(input: List<String>): Long {
        val correction = 10000000000000
        val machines = makeMachines(input)
        val validMachineResults = machines.mapNotNull { machine ->
            val adjustedPrizeX = machine.prize.first + correction
            val adjustedPrizeY = machine.prize.second + correction
            val match = solveLinearEquations(
                machine.buttonA,
                machine.buttonB,
                Pair(adjustedPrizeX, adjustedPrizeY)
            )
            match?.let { it.first * 3 + it.second }
        }
        return validMachineResults.sum()
    }

    val testInput = readInput("Day13_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
