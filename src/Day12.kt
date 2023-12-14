import kotlin.time.measureTimedValue

const val nonDamagedPattern = ".?"
const val damagedPattern = "#?"
typealias CacheKeyDay12 = Pair<String, List<Int>>

fun main() {

    fun count(spring: String, damagedSizes: List<Int>, cache: MutableMap<CacheKeyDay12, Long>): Long {
        if (spring.isEmpty()) return if (damagedSizes.isEmpty()) 1 else 0
        if (damagedSizes.isEmpty()) return if ("#" in spring) 0 else 1
        val key = spring to damagedSizes
        if (key in cache) return cache[key]!!

        var count = 0L
        if (spring[0] in nonDamagedPattern) {
            count += count(spring.drop(1), damagedSizes, cache)
        }
        if (spring[0] in damagedPattern) {
            val isStillPatternToValidate = damagedSizes[0] <= spring.length
            val noOperationalSprings = '.' !in spring.take(damagedSizes[0])
            val allPatternIsConsumed = damagedSizes[0] == spring.length
            val nextSpringIsNotDamaged = spring.getOrElse(damagedSizes[0]) { '#' } != '#'
            if (isStillPatternToValidate && noOperationalSprings && (allPatternIsConsumed || nextSpringIsNotDamaged)) {
                count += count(spring.drop(damagedSizes[0] + 1), damagedSizes.drop(1), cache)
            }
        }
        cache[key] = count
        return count
    }

    fun part1(input: List<String>): Long {
        return input.map { line ->
            line.split(" ").run { first() to last().split(",").map { it.toInt() } }
        }.sumOf { (spring, damagedSizes) ->
            count(spring, damagedSizes, mutableMapOf())
        }
    }

    fun part2(input: List<String>): Long {
        return input.map { line ->
            line.split(" ")
                .run {
                    val spring = "${first()}?".repeat(5).removeSuffix("?")
                    val damagedSizes = "${last()},".repeat(5).removeSuffix(",").split(",").map { it.toInt() }
                    spring to damagedSizes
                }
        }.sumOf { (spring, damagedSizes) ->
            count(spring, damagedSizes, mutableMapOf())
        }

    }

    val testInput = readInput("day12_sample", "day12")
    check(part1(testInput) == 21L)
    check(part2(testInput) == 525_152L)
    val input = readInput("day12_input", "day12")
    measureTimedValue {
        part1(input)
    }.also { println("Part1 response: ${it.value} took ${it.duration}") }
    measureTimedValue {
        part2(input)
    }.also { println("Part2 response: ${it.value} took ${it.duration}") }

}