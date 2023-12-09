import kotlin.time.measureTimedValue

fun main() {
    val testInput = readInput("day10_sample", "day10")
    //checks
    val input = readInput("day10_input", "day10")
    measureTimedValue {
        //part1
    }.also { println("Part1 response: ${it.value} took ${it.duration}") }
    measureTimedValue {
        //part2
    }.also { println("Part2 response: ${it.value} took ${it.duration}") }
}