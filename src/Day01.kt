fun main() {

    val digitsAsString = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    fun getDigitFromLine(line: String, last: Boolean, includeStringDigits: Boolean): Int {
        for ((index, c) in if (last) line.withIndex().reversed() else line.withIndex()) {
            if (c.isDigit()) return c.digitToInt()
            else {
                if (includeStringDigits) {
                    for ((n, s) in digitsAsString.withIndex()) {
                        val startIndex = if (last) index - s.length + 1 else index
                        val endIndex = if (last) index + 1 else index + s.length
                        if (line.safeSubstring(startIndex, endIndex) == s) return n + 1
                    }
                }
            }
        }
        throw IllegalStateException("No digit found in line: $line")
    }

    fun solve(input: List<String>, includeStringDigits: Boolean): Int {
        return input.sumOf {
            val first = getDigitFromLine(it, false, includeStringDigits)
            val last = getDigitFromLine(it, true, includeStringDigits)
            "${first}${last}".toInt()
        }
    }


    val testInput = readInput("Day01_test", "day1")
    check(solve(testInput, false) == 142)
    val part2TestInput = readInput("Day01_test_part2", "day1")
    check(solve(part2TestInput, true) == 281)

    val input = readInput("Day01", "day1")
    solve(input, false).println()
    solve(input, true).println()
}
