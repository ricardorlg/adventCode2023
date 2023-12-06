import kotlin.system.measureTimeMillis

fun main() {

    data class Race(
        val time: Long,
        val recordDistance: Long,
    )

    fun getTotalBeatRecordWays(data: List<Race>): Long {
        return data.fold(1L){acc, race ->
            val toysCardHoldingInterval = (1..race.time)
            var count = 0L
            var incrementFound = false
            for (time in toysCardHoldingInterval) {
                val carDistance = time * (race.time - time)
                //if we already have found a beating record and on current time the car is not beating the record, we can stop
                if (incrementFound && carDistance <= race.recordDistance) break
                //if the car is not beating the record, we can skip this time
                if (carDistance <= race.recordDistance) continue
                incrementFound = true
                count++
            }
            acc * count
        }
    }

    fun part1(input: List<String>): Long {
        val data = input.chunked(2).flatMap { lines ->
            val times = numberRegex.findAll(lines[0]).map { it.value.toLong() }.toList()
            val distances = numberRegex.findAll(lines[1]).map { it.value.toLong() }.toList()
            times.zip(distances).map { Race(it.first, it.second) }
        }
        return getTotalBeatRecordWays(data)
    }

    fun part2(input: List<String>): Long {
        val data = input.chunked(2).map { lines ->
            val totalTime = lines[0].replace(nonNumberRegex, "").toLong()
            val totalDistance = lines[1].replace(nonNumberRegex, "").toLong()
            Race(totalTime, totalDistance)
        }
        return getTotalBeatRecordWays(data)
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