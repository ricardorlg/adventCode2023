import kotlin.time.measureTimedValue

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun mirrorLeft(): Direction {
        return when (this) {
            UP -> LEFT
            DOWN -> RIGHT
            LEFT -> UP
            RIGHT -> DOWN
        }
    }

    fun mirrorRight(): Direction {
        return when (this) {
            UP -> RIGHT
            DOWN -> LEFT
            LEFT -> DOWN
            RIGHT -> UP
        }
    }
}

fun main() {
    data class Pos2D(val row: Int, val col: Int) {
        fun move(direction: Direction): Pos2D {
            return when (direction) {
                Direction.UP -> Pos2D(row - 1, col)
                Direction.DOWN -> Pos2D(row + 1, col)
                Direction.LEFT -> Pos2D(row, col - 1)
                Direction.RIGHT -> Pos2D(row, col + 1)
            }
        }
    }

    data class Beam(val pos: Pos2D, val direction: Direction) {
        fun nextMove(grid: List<String>): Pair<Beam, Beam?>? {
            val newPos = pos.move(direction)
            if (newPos.row < 0 || newPos.row >= grid.size || newPos.col < 0 || newPos.col >= grid[0].length) {
                return null
            }
            return when (val nextPosChar = grid[newPos.row][newPos.col]) {
                '.' -> Pair(Beam(newPos, direction), null)
                '\\' -> Pair(Beam(newPos, direction.mirrorLeft()), null)
                '/' -> Pair(Beam(newPos, direction.mirrorRight()), null)
                '|' -> if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                    Pair(Beam(newPos, Direction.UP), Beam(newPos, Direction.DOWN))
                } else {
                    Pair(Beam(newPos, direction), null)
                }

                '-' -> if (direction == Direction.UP || direction == Direction.DOWN) {
                    Pair(Beam(newPos, Direction.LEFT), Beam(newPos, Direction.RIGHT))
                } else {
                    Pair(Beam(newPos, direction), null)
                }

                else -> throw IllegalStateException("Unknown char $nextPosChar")
            }
        }
    }

    fun doSimulation(startBeam: Beam, grid: List<String>): Int {
        val queue = mutableListOf(startBeam)
        val seen = mutableSetOf<Beam>()
        while (queue.isNotEmpty()) {
            val beam = queue.removeFirst()
            val (updatedBeam, newBeam) = beam.nextMove(grid) ?: continue
            if (updatedBeam !in seen) {
                seen.add(updatedBeam)
                queue.add(updatedBeam)
            }
            if (newBeam != null && newBeam !in seen) {
                seen.add(newBeam)
                queue.add(newBeam)
            }
        }
        return seen.distinctBy { it.pos }.size
    }

    fun part1(input: List<String>): Int {
        val startBeam = Beam(Pos2D(0, -1), Direction.RIGHT)
        return doSimulation(startBeam, input)
    }

    fun part2(input: List<String>): Int {
        val topStartBeams = input[0].indices.map { col ->
            Beam(Pos2D(-1, col), Direction.DOWN)
        }
        val bottomStartBeams = input[0].indices.map { col ->
            Beam(Pos2D(input.size, col), Direction.UP)
        }
        val leftStartBeams = input.indices.map { row ->
            Beam(Pos2D(row, -1), Direction.RIGHT)
        }
        val rightStartBeams = input.indices.map { row ->
            Beam(Pos2D(row, input[0].length), Direction.LEFT)
        }

        val all = topStartBeams + bottomStartBeams + leftStartBeams + rightStartBeams

        return all.parallelStream().map { beam ->
            doSimulation(beam, input)
        }.max(Int::compareTo).get()
    }

    val testInput = readInput("day16_sample", "day16")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)
    val input = readInput("day16_input", "day16")
    measureTimedValue {
        part1(input)
    }.also { println("Part1 response: ${it.value} took ${it.duration}") }
    measureTimedValue {
        part2(input)
    }.also { println("Part2 response: ${it.value} took ${it.duration}") }
}