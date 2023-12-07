import HandType.*
import kotlin.time.measureTime

enum class HandType(val value: Int) {
    HIGH_CARD(1),
    ONE_PAIR(2),
    TWO_PAIR(3),
    THREE_OF_A_KIND(4),
    FULL_HOUSE(5),
    FOUR_OF_A_KIND(6),
    FIVE_OF_A_KIND(7)
}

fun main() {
    data class Hand(
        val cards: String,
        val bid: Long,
        val hasJoker: Boolean = false
    ) : Comparable<Hand> {
        val cardsValues = mapOf(
            '2' to 1,
            '3' to 2,
            '4' to 3,
            '5' to 4,
            '6' to 5,
            '7' to 6,
            '8' to 7,
            '9' to 8,
            'T' to 9,
            'J' to if (hasJoker) 0 else 10,
            'Q' to 11,
            'K' to 12,
            'A' to 13,
        )

        val cardsFrequencyWithJoker = cards.groupingBy { it }.eachCount()
        val jokerCount = cardsFrequencyWithJoker['J'] ?: 0
        val cardsFrequencyWithoutJoker = cardsFrequencyWithJoker.toMutableMap().apply { remove('J') }.toMap()
        val handType by lazy { if (hasJoker) getHandTypeWithJoker() else getHandTypeWithoutJoker() }

        private fun getHandTypeWithoutJoker(): HandType {
            return when {
                cardsFrequencyWithJoker.values.all { it == 1 } -> HIGH_CARD
                cardsFrequencyWithJoker.values.count { it == 1 } == 3 -> ONE_PAIR
                cardsFrequencyWithJoker.values.count { it == 2 } == 2 -> TWO_PAIR
                cardsFrequencyWithJoker.values.count { it == 1 } == 2 -> THREE_OF_A_KIND
                cardsFrequencyWithJoker.values.count { it == 3 } == 1 -> FULL_HOUSE
                cardsFrequencyWithJoker.values.any { it == 4 } -> FOUR_OF_A_KIND
                else -> FIVE_OF_A_KIND
            }
        }

        private fun getHandTypeWithJoker(): HandType {
            return when (jokerCount) {
                0 -> getHandTypeWithoutJoker()
                1 -> {
                    when {
                        cardsFrequencyWithoutJoker.values.any { it == 4 } -> FIVE_OF_A_KIND
                        cardsFrequencyWithoutJoker.values.count { it == 3 } == 1 -> FOUR_OF_A_KIND
                        cardsFrequencyWithoutJoker.values.count { it == 2 } == 2 -> FULL_HOUSE
                        cardsFrequencyWithoutJoker.values.count { it == 2 } == 1 -> THREE_OF_A_KIND
                        else -> ONE_PAIR
                    }
                }
                2 -> {
                    when {
                        cardsFrequencyWithoutJoker.values.count { it == 3 } == 1 -> FIVE_OF_A_KIND
                        cardsFrequencyWithoutJoker.values.count { it == 2 } == 1 -> FOUR_OF_A_KIND
                        else -> THREE_OF_A_KIND
                    }
                }
                3 -> {
                    when {
                        cardsFrequencyWithoutJoker.values.count { it == 2 } == 1 -> FIVE_OF_A_KIND
                        else -> FOUR_OF_A_KIND
                    }
                }
                else -> FIVE_OF_A_KIND
            }
        }

        override fun compareTo(other: Hand): Int {
            return when {
                handType > other.handType -> 1
                handType < other.handType -> -1
                else -> {
                    val h1Cards = cards
                    val h2Cards = other.cards
                    h1Cards.indices.forEach {
                        val h1CardValue = cardsValues[h1Cards[it]] ?: error("Invalid card value")
                        val h2CardValue = cardsValues[h2Cards[it]] ?: error("Invalid card value")
                        if (h1CardValue > h2CardValue) return 1
                        if (h1CardValue < h2CardValue) return -1
                    }
                    return 0
                }
            }

        }

    }

    fun parseHand(input: List<String>, withJoker: Boolean = false): List<Hand> {
        return input.map {
            val (cards, bid) = it.split(" ")
            Hand(cards.trim(), bid.trim().toLong(), withJoker)
        }
    }

    fun solve(input: List<String>, withJoker: Boolean = false): Long {
        val hands = parseHand(input, withJoker)
        return hands
            .sorted()
            .foldIndexed(0L) { index, acc, hand ->
                acc + hand.bid * (index + 1)
            }
    }

    val testInput = readInput("day7_sample", "day7")
    check(solve(testInput) == 6440L)
    check(solve(testInput,true) == 5905L)
    val input = readInput("day7_input", "day7")
    measureTime {
        solve(input).println()
    }.also { println("Part1 took $it") }
    measureTime {
        solve(input,true).println()
    }.also { println("Part2 took $it") }
}