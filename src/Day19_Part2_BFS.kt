import kotlin.time.measureTimedValue

fun main() {
    fun findAllValidAcceptedCombinations(input: List<String>): Long {
        val workflows = parse(input).workflows
        val start = Triple("in", emptyList<Operation>(), "xmas".associateWith { (1..4000) })
        val result = mutableListOf<Long>()
        val queue = mutableListOf(start)
        while (queue.isNotEmpty()) {
            val (currentLabel, _, currentRanges) = queue.removeFirst()
            if (currentLabel == "A") {
                result.add(currentRanges.toList().fold(1L) { acc, (_, range) -> acc * range.count() })
            }
            val rules = workflows[currentLabel] ?: continue
            listOf(
                rules.getOrNull(0),
                rules.getOrNull(1),
                rules.getOrNull(2),
                rules.getOrNull(3)
            ).forEachIndexed { index, rule ->
                if (rule != null) {
                    var next = when (val ruleResult = rule.getRuleResult()) {
                        is RuleResult.MoveTo -> {
                            Triple(ruleResult.target, listOfNotNull(rule.conditionOperation()), currentRanges)
                        }

                        is RuleResult.Accepted -> {
                            Triple("A", listOfNotNull(rule.conditionOperation()), currentRanges)
                        }

                        is RuleResult.Rejected -> {
                            Triple("R", listOfNotNull(rule.conditionOperation()), currentRanges)
                        }

                        else -> error("Invalid result: $ruleResult")
                    }
                    val inverseOperations = (0 until index).mapNotNull { i ->
                        rules[i].conditionOperation()?.negate()
                    }
                    next = next.copy(second = inverseOperations + next.second)
                    val nextRange = processRange(next.second, next.third)
                    next = next.copy(third = nextRange)
                    queue.add(next)
                }
            }
        }
        return result.sum()
    }

    val testInput = readInput("day19_sample", "day19")
    check(findAllValidAcceptedCombinations(testInput) == 167409079868000L)
    val input = readInput("day19_input", "day19")
    measureTimedValue { findAllValidAcceptedCombinations(input) }
        .also { println("Part1 response: ${it.value} took ${it.duration}") }
}