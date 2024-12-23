fun main() {
    data class Robot(val x0: Int, val y0: Int, val vx: Int, val vy: Int)

    fun extractRobotFromLine(input: String): Robot? {
        val regex = """p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""".toRegex()
        val matchResult = regex.find(input)
        return matchResult?.destructured?.let {
            Robot(it.component1().toInt(), it.component2().toInt(), it.component3().toInt(), it.component4().toInt())
        }
    }

    fun calculateEndPosition(robot: Robot, seconds: Int, lx: Int, ly: Int): Pair<Int, Int> {
        val xRem = (robot.x0 + seconds * robot.vx) % lx
        val yRem = (robot.y0 + seconds * robot.vy) % ly
        val x = if (xRem >= 0) xRem else lx + xRem
        val y = if (yRem >= 0) yRem else ly + yRem
        return x to y
    }

    fun part1(input: List<String>): Long {
        val seconds = 100
        //val lx = 11
        //val ly = 7
        val lx = 101 // 101 tiles wide
        val ly = 103 // 103 tiles tall
        val robots = input.mapNotNull { extractRobotFromLine(it) }
        val endPositions = robots.map { robot ->
            calculateEndPosition(robot, seconds, lx, ly)
        }
        val halfX = lx / 2
        val halfY = ly / 2
        // example length 7 -> half 3    indexes 0 1 2    3    4 5 6
        val grouped = endPositions.groupBy {
            when {
                it.first < halfX && it.second < halfY -> "1"
                it.first > halfX && it.second < halfY -> "2"
                it.first < halfX && it.second > halfY -> "3"
                it.first > halfX && it.second > halfY -> "4"
                else -> "middle"
            }
        }
        // multiply size of each quadrant except the middle one
        return grouped.filterNot { it.key == "middle" }.values.fold(1L, { acc, list -> acc * list.size })
    }

    // print a grid of 101 tiles wide and 103 tiles tall
    fun printGrid(points: List<Pair<Int, Int>>) {
        val grid = List(103) { y ->
            List(101) { x ->
                if (points.contains(x to y)) "X" else "."
            }.joinToString("")
        }.joinToString("\n")
        println(grid)
    }

//    fun isXmasTree(points: List<Pair<Int, Int>>): Boolean {
//        // Sort the points by x and then y coordinates
//        val sortedPoints = points.sortedWith(compareBy({ it.first }, { it.second }))
//
//        // Use windowed to check consecutive groups of 10
//        return sortedPoints.windowed(10).any { window ->
//            // Check if all points in the window form an upward diagonal (y2 - y1 == 1, x2 - x1 == 1)
//            val isUpwardDiagonal = window.zipWithNext().all { (p1, p2) ->
//                (p2.first - p1.first == 1) && (p2.second - p1.second == 1)
//            }
//
//            // Check if all points in the window form a downward diagonal (y2 - y1 == -1, x2 - x1 == 1)
//            val isDownwardDiagonal = window.zipWithNext().all { (p1, p2) ->
//                (p2.first - p1.first == 1) && (p2.second - p1.second == -1)
//            }
//
//            isUpwardDiagonal || isDownwardDiagonal
//        }
//    }
    // Check if the points form a vertical line of (at least) 10 points
    fun isXmasTree(points: List<Pair<Int, Int>>): Boolean {
        val sortedPoints = points.sortedWith(compareBy({ it.first }, { it.second }))
        return sortedPoints.windowed(10).any { window ->
            val isVerticalLine = window.zipWithNext().all { (p1, p2) ->
                (p1.first == p2.first) && (p2.second - p1.second == 1)
            }
            if(isVerticalLine) {
                println(printGrid(sortedPoints))
            }
            isVerticalLine
        }
    }

    tailrec fun iterateRobots(robots: List<Robot>, seconds: Int, lx: Int, ly: Int): Int {
        println("Seconds: $seconds")
        val endPositions = robots.map { robot ->
            calculateEndPosition(robot, seconds, lx, ly)
        }
        return if (isXmasTree(endPositions)) seconds else iterateRobots(robots, seconds + 1, lx, ly)
    }

    fun part2(input: List<String>): Int {
        val lx = 101 // 101 tiles wide
        val ly = 103 // 103 tiles tall
        val robots = input.mapNotNull { extractRobotFromLine(it) }
        return iterateRobots(robots, 0, lx, ly)
    }

    val testInput = readInput("Day14_test")
    part1(testInput).println()

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
