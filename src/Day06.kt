import kotlin.math.ceil
import kotlin.math.floor
import kotlin.system.measureTimeMillis

fun main() {

    data class Race(
        val time: Long,
        val recordDistance: Long,
    )

    fun countBeatingRecordOptions(data: List<Race>): Long {
        return data.fold(1L) { acc, race ->
            val (r1, r2) = solveQuadraticEquation(-1, race.time, -race.recordDistance)
            val leftInterval = floor(r1).toLong() + 1
            val rightInterval = ceil(r2).toLong() - 1
            val total = rightInterval - leftInterval + 1
            acc * total
        }
    }

    fun part1(input: List<String>): Long {
        val data = input.chunked(2).flatMap { lines ->
            val times = numberRegex.findAll(lines[0]).map { it.value.toLong() }.toList()
            val distances = numberRegex.findAll(lines[1]).map { it.value.toLong() }.toList()
            times.zip(distances).map { Race(it.first, it.second) }
        }
        return countBeatingRecordOptions(data)
    }

    fun part2(input: List<String>): Long {
        val data = input.chunked(2).map { lines ->
            val totalTime = lines[0].replace(nonNumberRegex, "").toLong()
            val totalDistance = lines[1].replace(nonNumberRegex, "").toLong()
            Race(totalTime, totalDistance)
        }
        return countBeatingRecordOptions(data)
    }


    val testInput = readInput("day6_sample", "day6")
    check(part1(testInput) == 288L)
    check(part2(testInput) == 71503L)
    val input = readInput("day6_input", "day6")
    measureTimeMillis {
        part1(input).println()
    }.also { println("Part1 took ${it}ms") }
    measureTimeMillis {
        part2(input).println()
    }.also { println("Part2 took ${it}ms") }
}