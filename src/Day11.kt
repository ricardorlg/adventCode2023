import kotlin.math.abs
import kotlin.time.measureTimedValue


fun main() {

    data class Pos2D(val row: Int, val col: Int) {
        fun addOffset(rows: List<Int>, cols: List<Int>, multiplier: Int): Pos2D {
            return Pos2D(
                row + rows.count { row > it } * multiplier,
                col + cols.count { col > it } * multiplier
            )
        }

        fun distanceTo(other: Pos2D): Long {
            return abs(row.toLong() - other.row) + abs(col - other.col)
        }
    }


    fun findGalaxies(grid: List<List<Char>>): List<Pos2D> {
        val galaxies = mutableListOf<Pos2D>()
        for (i in grid.indices) {
            for (j in grid[0].indices) {
                if (grid[i][j] == '#') {
                    galaxies.add(Pos2D(i, j))
                }
            }
        }
        return galaxies
    }

    fun solve(input: List<String>, multiplier: Int = 2): Long {
        val grid = input.map { it.toList() }
        val rowsToExpand = grid.indices.filter { !grid[it].contains('#') }
        val colsToExpand = grid[0].indices.filter { col ->
            grid.indices.all { row ->
                grid[row][col] != '#'
            }
        }
        return findGalaxies(grid).map {
            it.addOffset(rowsToExpand, colsToExpand, multiplier - 1)
        }.allCombinedPairs()
            .sumOf { (a, b) ->
                a.distanceTo(b)
            }
    }


    val testP1Input = readInput("day11_sample", "day11")
    check(solve(testP1Input) == 374L)
    check(solve(testP1Input, 100) == 8410L)
    val input = readInput("day11_input", "day11")
    measureTimedValue {
        solve(input).println()
    }.also { println("Part1 response: ${it.value} took ${it.duration}") }
    measureTimedValue {
        solve(input, 1_000_000).println()
    }.also { println("Part2 response: ${it.value} took ${it.duration}") }
}