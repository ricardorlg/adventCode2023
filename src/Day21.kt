import kotlin.time.measureTimedValue

fun main() {

    data class Pos2D(val row: Int, val col: Int) {

        fun cardinalNeighbors(): List<Pos2D> = listOf(
            Pos2D(row - 1, col),
            Pos2D(row + 1, col),
            Pos2D(row, col - 1),
            Pos2D(row, col + 1)
        )

        fun insideGrid(grid: List<String>): Boolean {
            val size = grid.size
            val r = (row % size).let { n -> if (n < 0) n + size else n }
            val c = (col % size).let { n -> if (n < 0) n + size else n }
            return grid[r][c] != '#'
        }
    }

    fun part1(input: List<String>, days: Int): Long {
        val sourcePoint = input
            .withIndex()
            .first { it.value.contains("S") }
            .let { Pos2D(it.index, it.value.indexOf("S")) }

        val response = mutableSetOf<Pos2D>()
        val seen = mutableSetOf<Pos2D>()
        val q = mutableListOf(sourcePoint to days)

        while (q.isNotEmpty()) {
            val (point, daysLeft) = q.removeFirst()
            if (daysLeft % 2 == 0) response.add(point)
            if (daysLeft == 0) continue
            val neighbors = point
                .cardinalNeighbors()
                .filter { it.insideGrid(input) && it !in seen }
                .map {
                    seen.add(it)
                    it to daysLeft - 1
                }
            q.addAll(neighbors)
        }
        return response.size.toLong()
    }

    /*
        PART 2 Hack :/

        f(x) = garden plots in terms of x where x is equal to the number of days divided by 131 and a carry over of 65 days
        when x is 0 f(x) = part1(65 days)  first value where diamond covers all the input without clone it
        when x is 1 f(x) = part1(196) which is equals to count garden plots to 131 days plus 65 days -> 131*1 +65 -> second value where diamond covers all the input after cloning it 3 times
        when x is 2 f(x) = part1(327) which is equals to count garden plots to 262 days plus 65 days -> 131*2 +65 -> third value where diamond covers all the input ....
        since garden plots is always positive, we can assume f(-1) = f(1) and so on, which at then ends means f(x) is quadratic
         so f(x) = ax^2 + bx + c
         f(0) = part1(65 days) -> c = part1(65 days)
         f(1) = a+b+c -> a+b+part1(65 days) = part1(196) -> a+b = part1(196) - part1(65 days)
         f(2) = 4a+2b+c -> 4a+2b+part1(65 days) = part1(327) -> 4a+2b = part1(327) - part1(65 days)
         let's call part1(196) as e
         let's call part1(327) as f
         so if we solve the system of equations:
         a+b = e-c
         4a+2b = f-c
         2a = -2e + 2c + f - c -> 2a = -2e + c + f -> a = (-2e + c + f)/2
         b = e-c-a
     */

    fun part2(input: List<String>, days: Int): Long {
        val rem = days % 131
        val x = days / 131
        require(rem == 65) { "Only valid to the assumption of cover all input with diamond" }
        val c = part1(input, 65) //f(0)
        val e = part1(input, 196)//f(1)
        val f = part1(input, 327)//f(2)
        val a = (-2 * e + c + f) / 2
        val b = e - c - a
        return a * x * x + b * x + c
    }

    val testInput = readInput("day21_sample", "day21")
    check(part1(testInput, 6) == 16L)
    val input = readInput("day21_input", "day21")

    measureTimedValue {
        part1(input, 64)
    }.also { println("Part1 response: ${it.value} took ${it.duration}") }

    measureTimedValue {
        part2(input, 26501365)
    }.also { println("Part2 response: ${it.value} took ${it.duration}") }

}