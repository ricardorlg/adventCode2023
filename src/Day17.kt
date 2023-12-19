import java.util.*
import kotlin.time.measureTimedValue

enum class CrucibleDirection {
    UP, DOWN, LEFT, RIGHT;

    val inverse: CrucibleDirection by lazy {
        when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }
}

fun main() {

    data class Pos2D(val row: Int, val col: Int) {
        fun move(direction: CrucibleDirection): Pos2D {
            return when (direction) {
                CrucibleDirection.UP -> Pos2D(row - 1, col)
                CrucibleDirection.DOWN -> Pos2D(row + 1, col)
                CrucibleDirection.LEFT -> Pos2D(row, col - 1)
                CrucibleDirection.RIGHT -> Pos2D(row, col + 1)
            }
        }
    }

    fun parse(input: List<String>): List<IntArray> {
        return input.map { line ->
            line.map { it.digitToInt() }.toIntArray()
        }
    }

    data class Crucible(val heatLoss: Int, val pos2D: Pos2D, val direction: CrucibleDirection?, val movements: Int) {
        fun move(data: List<IntArray>): Crucible? {
            if (direction == null) return null
            val newPos = pos2D.move(direction)
            if (newPos.row < 0 || newPos.row >= data.size || newPos.col < 0 || newPos.col >= data[0].size) {
                return null
            }
            val nextHeatLoss = data[newPos.row][newPos.col]
            return Crucible(heatLoss + nextHeatLoss, newPos, direction, movements + 1)
        }

        fun changeDirection(newDirection: CrucibleDirection, data: List<IntArray>): Crucible? {
            return Crucible(heatLoss, pos2D, newDirection, 0).move(data)
        }

        fun isAtEnd(data: List<IntArray>): Boolean {
            return pos2D.row == data.size - 1 && pos2D.col == data[0].size - 1
        }

        fun withoutHeatLoss(): Crucible {
            return Crucible(0, pos2D, direction, movements)
        }
    }

    fun solve(input: List<String>, maxMovementsStraight: Int, minMovementsToRotate: Int, minMovementsToEnd: Int): Int {
        val data = parse(input)
        val start = Crucible(0, Pos2D(0, 0), null, 0)
        val priorityQueue = PriorityQueue { a: Crucible, b: Crucible -> a.heatLoss - b.heatLoss }
        priorityQueue.add(start)
        val seen = mutableSetOf<Crucible>()
        while (priorityQueue.isNotEmpty()) {
            val crucible = priorityQueue.poll()
            if (crucible.isAtEnd(data) && crucible.movements >= minMovementsToEnd) {
                return crucible.heatLoss
            }
            if (crucible.withoutHeatLoss() in seen) continue
            seen.add(crucible.withoutHeatLoss())
            if (crucible.movements < maxMovementsStraight && crucible.direction != null) {
                crucible.move(data)?.let { priorityQueue.add(it) }
            }
            if (crucible.movements >= minMovementsToRotate || crucible.direction == null) {
                CrucibleDirection.entries.forEach { direction ->
                    if (direction != crucible.direction && direction != crucible.direction?.inverse) {
                        crucible.changeDirection(direction, data)?.let { priorityQueue.add(it) }
                    }
                }
            }
        }
        throw IllegalStateException("No solution found")
    }


    fun part1(input: List<String>): Int {
        return solve(input, 3, 0, 0)
    }

    fun part2(input: List<String>): Int {
        return solve(input, 10, 4, 4)
    }


    val testInput = readInput("day17_sample", "day17")
    check(part1(testInput) == 102)
    check(part2(testInput) == 94)
    val input = readInput("day17_input", "day17")
    measureTimedValue {
        part1(input)
    }.also { println("Part1 response: ${it.value} took ${it.duration}") }
    measureTimedValue {
        part2(input)
    }.also { println("Part2 response: ${it.value} took ${it.duration}") }
}