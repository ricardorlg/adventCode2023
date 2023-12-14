import kotlin.time.measureTimedValue

fun main() {
    data class Pattern(private val patterns: List<String>) {
        private val transposedPatterns by lazy { patterns.transpose() }
        fun findReflectionIndex(transposed: Boolean = false): Long {
            val listToUse = if (transposed) transposedPatterns else patterns
            return listToUse
                .zipWithNext { a, b -> a == b }
                .withIndex()
                .find { (i, valid) ->
                    valid && listToUse.take(i + 1).reversed() equalsByMinSize listToUse.drop(i + 1)
                }?.index?.inc()?.toLong() ?: 0
        }

        fun findSmudgeReflection(transposed: Boolean = false): Long {
            val listToUse = if (transposed) transposedPatterns else patterns
            return listToUse.zipWithNext { a, b -> a countDiffChars b }
                .withIndex()
                .find { (i, diffCount) ->
                    if (diffCount > 1) return@find false
                    val leftList = listToUse.take(i + 1).reversed()
                    val rightList = listToUse.drop(i + 1)
                    val totalDiffCount = leftList.zip(rightList) { a, b -> a countDiffChars b }.sum()
                    totalDiffCount == 1
                }?.index?.inc()?.toLong() ?: 0
        }
    }

    fun parseInput(input: List<String>): List<Pattern> {
        return input.mapUntilEmpty {
            Pattern(it)
        }
    }

    fun part1(input: List<String>): Long {
        val patterns = parseInput(input)
        return patterns.sumOf {
            val horizontal = it.findReflectionIndex()
            val vertical = it.findReflectionIndex(true)
            100 * horizontal + vertical
        }
    }

    fun part2(input: List<String>): Long {
        val patterns = parseInput(input)
        return patterns.sumOf {
            val horizontal = it.findSmudgeReflection()
            val vertical = it.findSmudgeReflection(true)
            100 * horizontal + vertical
        }
    }


    val testInput = readInput("day13_sample", "day13")
    check(part1(testInput) == 405L)
    check(part2(testInput) == 400L)
    val input = readInput("day13_input", "day13")
    measureTimedValue { part1(input) }.also { println("Part1 response: ${it.value} took ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("Part2 response: ${it.value} took ${it.duration}") }
}