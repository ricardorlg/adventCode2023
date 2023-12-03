fun main() {

    fun isValidPosition(row: Int, column: Int, input: List<String>, isGearCheck: Boolean): Boolean {
        val isGearCheckValidation = { c: Char -> c == '*' }
        val isPartNumberValidation = { c: Char -> c != '.' }
        return row in input.indices
                && column in input[row].indices
                && if (isGearCheck) isGearCheckValidation(input[row][column]) else isPartNumberValidation(input[row][column])
    }

    fun solve(input: List<String>, isGearCheck: Boolean): Long {
        val adjMap = mutableMapOf<Pair<Int, Int>, MutableList<Long>>()
        input.forEachIndexed { row, line ->
            "\\d+".toRegex().findAll(line).forEach { matchPart ->
                val adjacentIndices = buildList {
                    val columnRange = (matchPart.range.first - 1..matchPart.range.last + 1)
                    add(row to matchPart.range.first - 1) // left
                    add(row to matchPart.range.last + 1) // right
                    addAll(columnRange.map { row - 1 to it }) // top
                    addAll(columnRange.map { row + 1 to it }) // bottom
                }
                adjacentIndices.forEach { (r, c) ->
                    if (isValidPosition(r, c, input, isGearCheck)) {
                        adjMap.getOrPut(r to c) { mutableListOf() }.add(matchPart.value.toLong())
                    }
                }
            }
        }
        return if (!isGearCheck) {
            adjMap.values.flatten().sum()
        } else {
            adjMap.values.sumOf {
                if (it.size == 2) {
                    it[0] * it[1]
                } else 0
            }
        }
    }


    val testInput = readInput("day3_sample", "day3")
    check(solve(testInput, false) == 4361L)
    check(solve(testInput, true) == 467835L)

    val input = readInput("day3_input", "day3")
    solve(input, false).println()
    solve(input, true).println()
}
