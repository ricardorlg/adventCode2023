fun main() {

    val numberRegex = "\\d+".toRegex()

    fun getTotalMatches(line: String): Int {
        val winningNumbersLine = line.substringAfter(":").substringBefore("|")
        val mineNumbersLine = line.substringAfter("|")
        val winningNumbers = numberRegex.findAll(winningNumbersLine).map { it.value.toInt() }.toList()
        val mineNumbers = numberRegex.findAll(mineNumbersLine).map { it.value.toInt() }.toList()
        return winningNumbers.intersect(mineNumbers.toSet()).size
    }

    fun part1(input: List<String>): Long {
        return input.sumOf { line ->
            val common = getTotalMatches(line)
            if (common == 0) 0 else 1L shl common - 1
        }
    }

    fun part2(input: List<String>): Long {
        val cards = input.indices.associate { it + 1 to 1L }.toMutableMap()
        input.forEachIndexed { index, line ->
            val totalMatches = getTotalMatches(line)
            if (totalMatches > 0) {
                val nextCardIndex = index + 2
                val cardCopies = cards[index + 1]!!
                (nextCardIndex until nextCardIndex + totalMatches).forEach { card ->
                    if (cards.containsKey(card)) {
                        cards[card] = cards[card]!! + cardCopies
                    }
                }
            }
        }
        return cards.values.sum()
    }

    val testInput = readInput("day4_sample", "day4")
    check(part1(testInput) == 13L)
    check(part2(testInput) == 30L)

    val input = readInput("day4_input", "day4")
    part1(input).println()
    part2(input).println()
}
