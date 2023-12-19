import Rule.Companion.parseRule
import RuleResult.Companion.parseResult
import kotlin.time.measureTimedValue

data class Operation(val part: Char, val operation: Char, val value: Int){
    fun negate(): Operation {
        return when (operation) {
            '>' -> Operation(part, '<', value+1)
            '<' -> Operation(part, '>', value-1)
            else -> throw Exception("Invalid operation: $operation")
        }
    }
}
data class Part(val x: Long, val m: Long, val a: Long, val s: Long)

data class Puzzle(val workflows: Map<String, List<Rule>>, val parts: List<Part>)


sealed interface RuleResult {
    data object Accepted : RuleResult
    data object Rejected : RuleResult
    data object Empty : RuleResult
    data class MoveTo(val target: String) : RuleResult
    companion object {
        fun String.parseResult(): RuleResult {
            return when (this) {
                "A" -> Accepted
                "R" -> Rejected
                else -> MoveTo(this)
            }
        }
    }
}

sealed interface Rule {
    fun getRuleResult(): RuleResult
    fun conditionOperation():Operation?=null
    fun evaluate(input: Part): Boolean
    data class SimpleRule(val result: RuleResult) : Rule {
        override fun getRuleResult(): RuleResult {
            return result
        }
        override fun evaluate(input: Part): Boolean {
            return true
        }
    }

    data class ComplexRule(val part: Char, val operation: Char, val value: Int, val result: RuleResult) : Rule {
        override fun conditionOperation(): Operation {
            return Operation(part, operation, value)
        }

        override fun getRuleResult(): RuleResult {
            return result
        }
        override fun evaluate(input: Part): Boolean {
            val partValue = when (part) {
                'x' -> input.x
                'm' -> input.m
                'a' -> input.a
                's' -> input.s
                else -> throw Exception("Invalid part: $part")
            }
            return when (operation) {
                '>' -> partValue > value
                '<' -> partValue < value
                else -> throw Exception("Invalid operation: $operation")
            }

        }
    }

    companion object {
        fun String.parseRule(): Rule {
            val complexRuleRegex = "([xmas])([<|>])(\\d+):(\\w+)".toRegex()
            if (complexRuleRegex.matches(this)) {
                val (part, operation, value, target) = complexRuleRegex.matchEntire(this)!!.destructured
                return ComplexRule(part[0], operation[0], value.toInt(), target.parseResult())
            } else {
                return SimpleRule(parseResult())
            }
        }
    }
}

fun parse(input: List<String>): Puzzle {
    val workflowRegex = "(\\w+)\\{(.*)}".toRegex()
    val workflows = input.takeWhile { it.isNotEmpty() }.associate { line ->
        val (name, rulesData) = workflowRegex.matchEntire(line)?.destructured
            ?: throw Exception("Invalid workflow line: $line")
        name to rulesData.split(",").map { it.parseRule() }
    }
    val parts = input.dropWhile { it.isNotEmpty() }.drop(1).map {
        val partRegex = "\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)\\}".toRegex()
        val (x, m, a, s) = partRegex.matchEntire(it)?.destructured
            ?: throw Exception("Invalid part line: $it")
        Part(x.toLong(), m.toLong(), a.toLong(), s.toLong())
    }
    return Puzzle(workflows, parts)
}

fun main() {
    fun part1(input: List<String>): Long {
        val puzzle = parse(input)
        val workflows = puzzle.workflows
        val parts = puzzle.parts
        val startFlowName = "in"
        val acceptedParts = mutableListOf<Part>()
        parts.forEach { part ->
            var currentFlowName = startFlowName
            while (true) {
                val currentFlowRule = workflows[currentFlowName] ?: throw Exception("Invalid flow name: $currentFlowName")
                val flowResult = currentFlowRule.first { it.evaluate(part)  }.getRuleResult()
                when (flowResult) {
                    RuleResult.Accepted -> {
                        acceptedParts.add(part)
                        break
                    }

                    RuleResult.Rejected -> {
                        break
                    }
                    is RuleResult.MoveTo -> {
                        currentFlowName = flowResult.target
                    }
                    RuleResult.Empty -> {
                        continue
                    }
                }
            }
        }
        return acceptedParts.sumOf {
            it.x + it.m + it.a + it.s
        }
    }


    val testInput = readInput("day19_sample", "day19")
    check(part1(testInput) == 19114L)
    val input = readInput("day19_input", "day19")
    measureTimedValue {
        part1(input)
    }.also { println("Part1 response: ${it.value} took ${it.duration}") }
}