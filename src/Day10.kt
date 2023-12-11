import kotlin.math.absoluteValue
import kotlin.time.measureTimedValue

fun main() {

    data class P2(val x: Int, val y: Int) {
        fun getNeighbors(c: Char): List<P2> {
            return when (c) {
                '|' -> listOf(P2(x + 1, y), P2(x - 1, y))
                '-' -> listOf(P2(x, y + 1), P2(x, y - 1))
                'L' -> listOf(P2(x - 1, y), P2(x, y + 1))
                'J' -> listOf(P2(x - 1, y), P2(x, y - 1))
                '7' -> listOf(P2(x + 1, y), P2(x, y - 1))
                'F' -> listOf(P2(x + 1, y), P2(x, y + 1))
                '.' -> emptyList()
                'S' -> listOf(P2(x + 1, y), P2(x - 1, y), P2(x, y + 1), P2(x, y - 1))
                else -> throw Exception("Unknown char $c")
            }
        }
    }

    fun P2.getPath(startingPoint: P2, adj: Map<P2, List<P2>>): Set<P2> {
        val path = mutableSetOf(startingPoint)
        var current = this
        while (true) {
            val pointNeighbours = adj.getOrDefault(current, emptyList())
            val nextPoint = pointNeighbours.firstOrNull { !path.contains(it) }
            path.add(current)
            if (nextPoint == null) {
                break
            }
            current = nextPoint
        }
        return path
    }

    fun getLoop(input: List<String>): List<P2> {
        val adj = mutableMapOf<P2, List<P2>>()
        val rows = input.size
        val cols = input[0].length
        var startingPoint: P2? = null
        for (x in 0 until rows) {
            for (y in 0 until cols) {
                val p = P2(x, y)
                val c = input[x][y]
                if (c != '.') {
                    adj[p] = p.getNeighbors(c)
                    if (c == 'S') {
                        startingPoint = p
                    }
                }
            }
        }
        require(startingPoint != null)
        return adj[startingPoint]
            ?.first { input[it.x][it.y] != '.' }
            ?.getPath(startingPoint, adj)
            ?.toList()
            .orEmpty()
    }

    fun part1(input: List<String>): Int {
        val loop = getLoop(input)
        return loop.size / 2
    }

    fun part2(input: List<String>): Int {
        val loop = getLoop(input).toMutableList().apply { add(first()) } //close the loop
        val p = loop.size
        val a = loop.zipWithNext { (x1, y1), (x2, y2) -> x1 * y2 - y1 * x2 }.sum().absoluteValue / 2
        return a - p / 2 + 1
    }


    val testP1Input = readInput("day10_sample", "day10")
    check(part1(testP1Input) == 8)
    val testP2Input = readInput("day10_p2_sample", "day10")
    check(part2(testP2Input) == 4)
    val input = readInput("day10_input", "day10")
    measureTimedValue {
        part1(input).println()
    }.also { println("Part1 response: ${it.value} took ${it.duration}") }
    measureTimedValue {
        part2(input).println()
    }.also { println("Part2 response: ${it.value} took ${it.duration}") }
}