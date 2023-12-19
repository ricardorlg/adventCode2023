import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.time.measureTimedValue

fun main() {

    data class Pos2D(val row: Long, val col: Long) {
        fun move(dir: Char, meters: Long): Pos2D {
            return when (dir) {
                'U' -> Pos2D(row - meters, col)
                'D' -> Pos2D(row + meters, col)
                'L' -> Pos2D(row, col - meters)
                'R' -> Pos2D(row, col + meters)
                else -> throw IllegalArgumentException("Unknown direction $dir")
            }
        }

        fun distanceTo(other: Pos2D): Long {
            return abs(row - other.row) + abs(col - other.col)
        }
    }

    data class Polygon(private val points: List<Pos2D>) {
        fun perimeter(): Long {
            return points.zipWithNext(Pos2D::distanceTo).sum()
        }

        fun area(): Long {
            return points.zipWithNext { (x1, y1), (x2, y2) -> x1 * y2 - y1 * x2 }.sum().absoluteValue / 2
        }

        fun totalPoints(): Long {
            return area() + perimeter() / 2 + 1
        }
    }

    fun List<Pos2D>.toPolygon(isClosed: Boolean): Polygon {
        return Polygon(if (isClosed) this else this + first())
    }

    data class Instruction(val dir: Char, val meters: Long)

    fun parse(input: List<String>, isPart2: Boolean = false): List<Instruction> {
        return input.map {
            val (op, meters, color) = it.split(" ")
            if (isPart2) {
                val fixedData = color.removeSurrounding("(", ")")
                val realMeters = fixedData.drop(1).dropLast(1).toLong(16)
                val realDirection = fixedData.drop(1).takeLast(1).toLong(16).run {
                    when (this) {
                        0L -> 'R'
                        1L -> 'D'
                        2L -> 'L'
                        3L -> 'U'
                        else -> throw IllegalArgumentException("Unknown direction $this")
                    }
                }
                Instruction(realDirection, realMeters)
            } else {
                Instruction(op.single(), meters.toLong())
            }
        }
    }

    fun solve(input: List<String>, isPart2: Boolean): Long {
        return parse(input, isPart2)
            .fold(mutableListOf(Pos2D(0, 0))) { acc, instruction ->
                val (dir, meters) = instruction
                acc.add(acc.last().move(dir, meters))
                acc
            }.toPolygon(false).totalPoints()
    }

    val testInput = readInput("day18_sample", "day18")
    check(solve(testInput, false) == 62L)
    check(solve(testInput, true) == 952408144115)
    val input = readInput("day18_input", "day18")
    measureTimedValue {
        solve(input, false)
    }.also {
        println("Part1 response: ${it.value} took ${it.duration}")
    }
    measureTimedValue {
        solve(input, true)
    }.also {
        println("Part2 response: ${it.value} took ${it.duration}")
    }

}