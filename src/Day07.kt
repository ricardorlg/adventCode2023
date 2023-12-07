import kotlin.time.measureTime

fun main() {

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
        'J' to 10,
        'Q' to 11,
        'K' to 12,
        'A' to 13,
    )

    val cardsValuesWithJoker = mapOf(
        '2' to 2,
        '3' to 3,
        '4' to 4,
        '5' to 5,
        '6' to 6,
        '7' to 7,
        '8' to 8,
        '9' to 9,
        'T' to 10,
        'J' to 1,
        'Q' to 11,
        'K' to 12,
        'A' to 13,
    )

    data class Hand(
        val cards: String,
        val bid: Long,
        val hasJoker:Boolean = false
    ) {
        val freqMap by lazy { cards.groupingBy { it }.eachCount() }
        val handType by lazy { if(hasJoker) getHandTypeWithJoker() else calculateHandType() }

       private fun calculateHandType(): Int {
            return when {
                freqMap.values.all { it == 1 } -> 1
                freqMap.values.count { it == 1 } == 3 -> 2
                freqMap.values.count { it == 2 } == 2 -> 3
                freqMap.values.count { it == 1 } == 2 -> 4
                freqMap.values.count { it == 3 } == 1 -> 5
                freqMap.values.any { it == 4 } -> 6
                else -> 7
            }
        }

       private fun getHandTypeWithJoker(): Int {
            val jokerCount = freqMap['J'] ?: 0
            if (jokerCount == 0) return calculateHandType()
            return when (jokerCount) {
                1 -> {
                    when {
                        freqMap.values.any { it == 4 } -> 7
                        freqMap.values.count { it == 3 } == 1 -> 6
                        freqMap.values.count { it == 2 } == 2 -> 5
                        freqMap.values.count { it == 2 } == 1 -> 4
                        else -> 2
                    }
                }

                2 -> {
                    when {
                        freqMap.values.count { it == 3 } == 1 -> 7
                        freqMap.values.count { it == 2 } == 1 -> 6
                        else -> 4
                    }
                }

                3 -> {
                    when {
                        freqMap.values.count { it == 2 } == 1 -> 7
                        else -> 6
                    }
                }

                else -> 7
            }
        }

        fun getHandCardsScore(): Int {
            return cards.map { cardsValues[it]!! }.sum()
        }

        fun getHandCardsScoreWithJoker(): Int {
            return cards.map { cardsValuesWithJoker[it]!! }.sum()
        }

    }

    fun parseHand(input: List<String>, withJoker:Boolean=false): List<Hand> {
        return input.map {
            val (cards, bid) = it.split(" ")
            Hand(cards.trim(), bid.trim().toLong(),withJoker)
        }
    }

    val comp = Comparator<Hand> { h1, h2 ->
        val h1Cards = h1.cards
        val h2Cards = h2.cards
        h1Cards.indices.forEach {
            val h1CardValue = cardsValues[h1Cards[it]]!!
            val h2CardValue = cardsValues[h2Cards[it]]!!
            if (h1CardValue > h2CardValue) return@Comparator 1
            if (h1CardValue < h2CardValue) return@Comparator -1
        }
        return@Comparator 0
    }

    val compWithJoker = Comparator<Hand> { h1, h2 ->
        val h1Cards = h1.cards
        val h2Cards = h2.cards
        h1Cards.indices.forEach {
            val h1CardValue = cardsValuesWithJoker[h1Cards[it]]!!
            val h2CardValue = cardsValuesWithJoker[h2Cards[it]]!!
            if (h1CardValue > h2CardValue) return@Comparator 1
            if (h1CardValue < h2CardValue) return@Comparator -1
        }
        return@Comparator 0
    }


    fun part1(input: List<String>): Long {
        val hands = parseHand(input)
        val total = hands.size
        return hands
            .sortedWith(compareByDescending<Hand> { it.handType }.thenDescending(comp))
            .foldIndexed(0L) { index, acc, hand ->
                acc + hand.bid * (total - index)
            }
    }
    fun part2(input: List<String>): Long {
        val hands = parseHand(input,true)
        val total = hands.size
        return hands
            .sortedWith(compareByDescending<Hand> {hand-> hand.handType }.thenDescending(compWithJoker))
            .onEach(::println)
            .foldIndexed(0L) { index, acc, hand ->
                acc + hand.bid * (total - index)
            }
    }

    val testInput = readInput("day7_sample", "day7")
    check(part1(testInput) == 6440L)
    println("--------------")
    println(part2(testInput))
//    check(part2(testInput) == 71503L)
    val input = readInput("day7_input", "day7")
    measureTime {
        part1(input).println()
    }.also { println("Part1 took $it") }
//    measureTime {
//        part2(input).println()
//    }.also { println("Part2 took $it") }
}