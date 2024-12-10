private typealias Trail = List<Cell<Int>>
fun main() {

    fun makeGrid(input: List<String>): Grid2D<Int> =
        Grid2D(input.map { it.chunked(1).map { it.toInt() } })

    fun calculateTrailsForHead(mapGrid: Grid2D<Int>, trailHead: Cell<Int>, trail: Trail): List<Trail> {
        fun recurse(current: Cell<Int>, trail: Trail, validTrails: MutableList<Trail>, visited: MutableSet<Cell<Int>>) {
            if (current.value == 9) {
                validTrails.add(trail)
                return
            }
            visited.add(current) // not really needed as we only go up, but to avoid cycles
            val nextCells = mapGrid.getAdjacent(current.x, current.y)
                .filter { it.value == current.value + 1 && it !in visited }
            for (nextCell in nextCells) {
                recurse(nextCell, trail + nextCell, validTrails, visited)
            }
            visited.remove(current) // Unmark current cell to allow other paths. Note implicit return of Unit
        }

        val validTrails = mutableListOf<Trail>()
        val visited = mutableSetOf<Cell<Int>>()
        recurse(trailHead, trail, validTrails, visited)
        return validTrails
    }

    fun calculateTrails(mapGrid: Grid2D<Int>): List<Trail> {
        val trailHeads = mapGrid.getAllCells().filter { it.value == 0 }
        return trailHeads.map { calculateTrailsForHead(mapGrid, it, listOf(it)) }.flatten()
    }

    fun part1(input: List<String>): Int {
        val mapGrid = makeGrid(input)
        val allTrails= calculateTrails(mapGrid)
        // count trails with unique start and end points, so first and last element of trail combination must be unique
        val startEnd = allTrails.map { it.first() to it.last() }
        return startEnd.distinct().size
    }

    fun part2(input: List<String>): Int {
        val mapGrid = makeGrid(input)
        return calculateTrails(mapGrid).distinct().size
    }

    val testInput = readInput("Day10_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
