enum class Color {
    RED, GREEN, BLUE
}

fun main() {

    data class Cube(
        val color: Color,
        val value: Long
    )

    data class Game(
        val gameId: Int,
        val revealedCubes: List<List<Cube>>
    ) {
        fun isPossible(maxRedCubes: Int, maxGreenCubes: Int, maxBlueCubes: Int): Boolean {
            val redCubes = revealedCubes.flatten().filter { it.color == Color.RED }
            val greenCubes = revealedCubes.flatten().filter { it.color == Color.GREEN }
            val blueCubes = revealedCubes.flatten().filter { it.color == Color.BLUE }
            return redCubes.none { it.value > maxRedCubes } &&
                    greenCubes.none { it.value > maxGreenCubes } &&
                    blueCubes.none { it.value > maxBlueCubes }
        }

        val redCubes get() = revealedCubes.flatten().filter { it.color == Color.RED }
        val greenCubes get() = revealedCubes.flatten().filter { it.color == Color.GREEN }
        val blueCubes get() = revealedCubes.flatten().filter { it.color == Color.BLUE }
    }

    fun parseInput(input: List<String>): List<Game> {
        return input.map { line ->
            val gameIdInfo = line.substringBefore(":").trim()
            val gameId = gameIdInfo.substringAfter(" ").trim().toInt()
            val revealedCubesInfo = line.substringAfter(":").trim()
            val revealedCubes = revealedCubesInfo.split(";").map { r ->
                r.split(",").map {
                    val number = it.trim().substringBefore(" ").toLong()
                    val color = it.trim().substringAfter(" ").trim()
                    Cube(Color.valueOf(color.uppercase()), number)
                }
            }
            Game(gameId, revealedCubes)
        }
    }

    fun part1(input: List<String>): Int {
        val maxRedCubes = 12
        val maxGreenCubes = 13
        val maxBlueCubes = 14
        val games = parseInput(input)
        return games.sumOf { game -> if (game.isPossible(maxRedCubes, maxGreenCubes, maxBlueCubes)) game.gameId else 0 }
    }

    fun part2(input: List<String>): Long {
        val games = parseInput(input)
        return games.sumOf { game ->
            val minimumRedCubes = game.redCubes.maxOf { it.value }
            val minimumGreenCubes = game.greenCubes.maxOf { it.value }
            val minimumBlueCubes = game.blueCubes.maxOf { it.value }
            minimumRedCubes * minimumGreenCubes * minimumBlueCubes
        }
    }


    val testInput = readInput("day2_sample", "day2")
    part1(testInput)
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286L)

    val input = readInput("day2_input", "day2")
    part1(input).println()
    part2(input).println()
}
