import kotlin.time.measureTimedValue

fun main() {

    fun List<Long>.nextDiffs() = windowed(2, 1, false).map { it[1] - it[0] }


    fun List<Long>.allEqual() = distinct().size == 1


    fun nextOasisValue(initial: List<Long>): Long {
        val lastValues = mutableListOf(initial.last())
        var next = initial
        while (true) {
            val currentDiffs = next.nextDiffs()
            lastValues.add(currentDiffs.last())
            if (currentDiffs.allEqual()) {
                break
            }
            next = currentDiffs
        }
        return lastValues.sum()
    }

    fun prevOasisValue(initial: List<Long>): Long {
        val firstValues = mutableListOf(initial.first())
        var next = initial
        while (true) {
            val currentDiffs = next.nextDiffs()
            firstValues.add(currentDiffs.first())
            if (currentDiffs.allEqual()) {
                break
            }
            next = currentDiffs
        }
        return firstValues.foldRight(0) { acc, l -> acc - l }
    }

    fun solve(input: List<String>, isPart2: Boolean): Long {
        val history = input.map { line -> numberRegex.findAll(line).map { it.value.toLong() }.toList() }
        return history.sumOf { oasisValues ->
            if (isPart2) prevOasisValue(oasisValues) else nextOasisValue(oasisValues)
        }
    }

    val testInput = readInput("day9_sample", "day9")
    check(solve(testInput, false) == 114L)
    check(solve(testInput, true) == 2L)
    val input = readInput("day9_input", "day9")
    measureTimedValue {
        solve(input, false)
    }.also { println("Part1 response: ${it.value} took ${it.duration}") }
    measureTimedValue {
        solve(input, true)
    }.also { println("Part2 response: ${it.value} took ${it.duration}") }
}