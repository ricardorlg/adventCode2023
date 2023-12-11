fun main() {

    fun expand(input: List<CharArray>): List<CharArray> {
        val rowToFill = List(input[0].size) { '.' }
        val rowsToDuplicate = mutableListOf<Int>()
        val colsToDuplicate = mutableListOf<Int>()
        for (i in input.indices) {
            val row = input[i]
            if (row.all { it == '.' }) {
                rowsToDuplicate.add(i)
            }
        }
        val newInput = input.toMutableList()
        for (i in rowsToDuplicate) {
            newInput.add(i + 1, rowToFill.toCharArray())
        }
        for (j in newInput[0].indices) {
            for (i in newInput.indices) {
                if (newInput[i][j] != '.') {
                    break
                }
                if (i == newInput.size - 1) {
                    colsToDuplicate.add(j)
                }
            }
        }
        for (i in colsToDuplicate) {
            for (j in newInput[0].indices) {
                if (j == i + 1) {
                    for (k in newInput.indices) {
                        newInput[k] = newInput[k].toMutableList().apply { add(j, '.') }.toCharArray()
                    }
                }
            }
        }
        return newInput
    }

    fun part1(input: List<String>): Int {
        val grid = expand(input.map { it.toCharArray() })
        val galaxies = mutableListOf<Pair<Int, Int>>()
        for (i in grid.indices) {
            for (j in grid[0].indices) {
                if (grid[i][j] == '#') {
                    galaxies.add(Pair(i, j))
                }
            }
        }

        return 0
    }


    val testP1Input = readInput("day11_sample", "day11")
    part1(testP1Input)
//    check(part1(testP1Input) == 8)
//    check(part2(testP2Input) == 4)
//    val input = readInput("day10_input", "day10")
//    measureTimedValue {
//        part1(input).println()
//    }.also { println("Part1 response: ${it.value} took ${it.duration}") }
//    measureTimedValue {
//        part2(input).println()
//    }.also { println("Part2 response: ${it.value} took ${it.duration}") }

}