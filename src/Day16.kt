import java.util.*
import kotlin.math.abs

private typealias Grid = List<List<Char>>
private typealias Path = List<Pair<Int, Int>>
private typealias CostPath = Pair<Path, Int>

val directions = mapOf(
    "up" to Pair(-1, 0),
    "down" to Pair(1, 0),
    "left" to Pair(0, -1),
    "right" to Pair(0, 1)
)

val turns = mapOf(
    "up" to listOf("up", "left", "right"),
    "down" to listOf("down", "left", "right"),
    "left" to listOf("left", "up", "down"),
    "right" to listOf("right", "up", "down")
)

fun main() {

    data class State(
        val x: Int,
        val y: Int,
        val direction: String,
        val path: List<Pair<Int, Int>>,
        val cost: Int
    )

    // Heuristic function: Manhattan distance
    fun heuristic(x: Int, y: Int): Int {
        val endX = 139
        val endY = 1
        return abs(x - endX) + abs(y - endY)
    }

    fun findOptimalPath(grid: Grid): CostPath? {
        val rows = grid.size
        val cols = grid[0].size

        // Helper function to validate a cell
        fun isValid(x: Int, y: Int): Boolean =
            x in 0 until rows && y in 0 until cols && grid[x][y] in listOf('.', 'E')

        val start = grid.flatMapIndexed { i, row ->
            row.mapIndexedNotNull { j, cell -> if (cell == 'S') i to j else null }
        }.firstOrNull() ?: error("Start point 'S' not found")

        // Priority queue for A* search
        val pq = PriorityQueue<State>(compareBy { it.cost + heuristic(it.x, it.y) })

        // Initialize the priority queue with all possible initial moves
        directions.forEach { (initialDirection, delta) ->
            val (dx, dy) = delta
            val nx = start.first + dx
            val ny = start.second + dy

            if (isValid(nx, ny)) {
                pq.add(
                    State(
                        x = nx,
                        y = ny,
                        direction = initialDirection,
                        path = listOf(start, nx to ny),
                        cost = if (initialDirection == "right") 1 else 1001
                    )
                )
            }
        }

        // Map to track the minimum cost at each state
        val visitedCosts = mutableMapOf<Triple<Int, Int, String>, Int>()

        while (pq.isNotEmpty()) {
            val current = pq.poll()

            // If reached the endpoint, return the result
            if (grid[current.x][current.y] == 'E') {
                return current.path to current.cost
            }

            // Prune paths with higher cost
            val stateKey = Triple(current.x, current.y, current.direction)
            if (visitedCosts[stateKey]?.let { it <= current.cost } == true) continue
            visitedCosts[stateKey] = current.cost

            // Explore all possible moves
            turns[current.direction]?.forEach { nextDirection ->
                val (dx, dy) = directions[nextDirection]!!
                val nx = current.x + dx
                val ny = current.y + dy
                val moveCost = if (nextDirection == current.direction) 1 else 1001

                if (isValid(nx, ny)) {
                    pq.add(
                        State(
                            x = nx,
                            y = ny,
                            direction = nextDirection,
                            path = current.path + (nx to ny),
                            cost = current.cost + moveCost
                        )
                    )
                }
            }
        }

        return null // No path found
    }

    fun part1(input: List<String>): Int {
        val grid = input.map { it.toList() }
        val optimalPath = findOptimalPath(grid)
        return optimalPath?.second ?: 0
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readInput("Day16_test")
    //part1(testInput).println()
    //part2(testInput).println()

    val input = readInput("Day16")
    part1(input).println()
    //part2(input).println()
}
