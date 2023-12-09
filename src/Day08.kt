import kotlin.time.measureTime

typealias NodeNeighbors = Pair<String, String>

fun main() {


    fun NodeNeighbors.getNodeNeighbourByDirection(direction: Char): String {
        return when (direction) {
            'L' -> first
            'R' -> second
            else -> throw IllegalArgumentException("Invalid direction $direction")
        }
    }

    fun parseInput(input: List<String>): Pair<Sequence<Char>, Map<String, NodeNeighbors>> {
        val instructions = input[0].asIterable().toCircularSequence()
        val network = input.drop(2).associate { line ->
            val (label, leftNodeLabel, rightNodeLabel) = line.split("=", ",")
                .map { it.trim().removePrefix("(").removeSuffix(")") }
            label to NodeNeighbors(leftNodeLabel, rightNodeLabel)
        }
        return instructions to network
    }

    fun getPathLength(
        network: Map<String, NodeNeighbors>,
        startingNodeLabel: String,
        instructions: Sequence<Char>,
        endCondition: (String) -> Boolean
    ): Long {
        var currentNodeLabel: String? = startingNodeLabel
        var steps = 0L
        val instructionsIterator = instructions.iterator()
        while (true) {
            val instruction = instructionsIterator.next()
            currentNodeLabel = network[currentNodeLabel]?.getNodeNeighbourByDirection(instruction)
            steps++
            if (endCondition(currentNodeLabel.orEmpty())) {
                break
            }
        }
        return steps
    }

    fun part1(input: List<String>): Long {
        val (instructions, network) = parseInput(input)
        return getPathLength(network, "AAA", instructions) { it == "ZZZ" }
    }

    fun part2(input: List<String>): Long {
        val (instructions, network) = parseInput(input)
        val startingNodes = network.keys.filter { it.endsWith("A") }
        return startingNodes.map { node ->
            getPathLength(
                network = network,
                startingNodeLabel = node,
                instructions = instructions
            ) { it.endsWith("Z") }
        }.lcm()
    }

    val testInput = readInput("day8_sample", "day8")
    check(part1(testInput) == 2L)
    val testInput2 = readInput("day8_2_sample", "day8")
    check(part2(testInput2) == 6L)
    val input = readInput("day8_input", "day8")
    measureTime {
        part1(input).println()
    }.also { println("Part1 took $it") }
    measureTime {
        part2(input).println()
    }.also { println("Part2 took $it") }
}