import kotlin.time.measureTimedValue

fun main() {

    fun String.hashAlgorithm(): Int {
        return fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }
    }


    data class Lens(val label: String, val focalLength: Int) {
        val hash = label.hashAlgorithm()

        fun updateFocalLength(newFocalLength: Int): Lens {
            return Lens(label, newFocalLength)
        }

        fun focusingPower(index: Int): Long {
            return (1L + hash) * (index + 1) * focalLength
        }
    }

    fun List<Lens>.totalFocusingPower(): Long {
        return withIndex().sumOf { (index, lens) -> lens.focusingPower(index) }
    }

    fun part1(input: String): Long {
        return input
            .split(",")
            .sumOf { it.hashAlgorithm().toLong() }
    }

    fun part2(input: String): Long {
        return input
            .split(",")
            .map {
                if (it.endsWith("-")) {
                    Triple(it.dropLast(1), null, '-')
                } else {
                    val label = it.substringBeforeLast("=")
                    val focalLength = it.substringAfterLast("=").toInt()
                    Triple(label, focalLength, '=')
                }
            }.fold(List(256) { mutableListOf<Lens>() }) { boxes, (label, focalLength, op) ->
                boxes.apply {
                    val box = label.hashAlgorithm()
                    if (op == '-') {
                        this[box].removeIf { lens -> lens.label == label }
                    } else {
                        val indexOfLens = this[box].indexOfFirst { lens -> lens.label == label }
                        if (indexOfLens != -1) {
                            this[box][indexOfLens] = this[box][indexOfLens].updateFocalLength(focalLength!!)
                        } else {
                            this[box].add(Lens(label, focalLength!!))
                        }
                    }
                }
            }.sumOf { it.totalFocusingPower() }
    }

    val testInput = readInputString("day15_sample", "day15")
    check(part1(testInput) == 1320L)
    check(part2(testInput) == 145L)
    val input = readInputString("day15_input", "day15")
    measureTimedValue {
        part1(input)
    }.also { println("Part1 response: ${it.value} took ${it.duration}") }
    measureTimedValue {
        part2(input)
    }.also { println("Part2 response: ${it.value} took ${it.duration}") }
}