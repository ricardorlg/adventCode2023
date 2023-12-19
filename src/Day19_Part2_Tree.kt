import kotlin.time.measureTimedValue

fun processRange(operations: List<Operation>, ranges: Map<Char, IntRange>): Map<Char, IntRange> {
    val newRanges = ranges.toMutableMap()
    operations.forEach { operation ->
        val (part, op, value) = operation
        when (op) {
            '<' -> {
                val min = newRanges[part]!!.first
                val newRange = min until value
                newRanges[part] = newRange
            }

            '>' -> {
                val max = newRanges[part]!!.last
                val newRange = value + 1..max
                newRanges[part] = newRange
            }

            else -> error("Invalid op: $op")

        }
    }

    return newRanges
}

fun main() {

    data class NodeInfo(
        val label: String,
        val trueCondition: Operation? = null,
        var ranges: Map<Char, IntRange> = emptyMap()
    ) {
        private val falseConditions: MutableList<Operation> = mutableListOf()

        private val conditions by lazy {
            falseConditions + listOfNotNull(trueCondition)
        }

        fun updateValidRanges(ranges: Map<Char, IntRange>) {
            this.ranges = processRange(conditions, ranges)
        }

        fun addFalseCondition(condition: Operation?) {
            if (condition != null) {
                falseConditions.add(condition.negate())
            }
        }
    }

    data class Day19TreeNode(
        val value: NodeInfo,
        private var firstChild: Day19TreeNode? = null,
        private var secondChild: Day19TreeNode? = null,
        private var thirdChild: Day19TreeNode? = null,
        private var lastChild: Day19TreeNode? = null
    ) {
        fun children(): List<Day19TreeNode> {
            return listOfNotNull(firstChild, secondChild, thirdChild, lastChild)
        }

        fun addChild(node: Day19TreeNode) {
            if (firstChild == null) {
                firstChild = node
            } else if (secondChild == null) {
                secondChild = node
            } else if (thirdChild == null) {
                thirdChild = node
            } else if (lastChild == null) {
                lastChild = node
            } else {
                error("Invalid node: $node")
            }
        }

        fun isAccepted(): Boolean {
            return value.label == "A"
        }
    }


    data class Day19Tree(val root: Day19TreeNode) {

        fun allAcceptedPaths(): List<Pair<List<NodeInfo>, Long>> {
            val paths = mutableListOf<Pair<List<NodeInfo>, Long>>()
            fun traverse(node: Day19TreeNode, path: List<NodeInfo>) {
                if (node.isAccepted()) {
                    val pathCombinations = node
                        .value
                        .ranges
                        .toList()
                        .fold(1L) { acc, (_, range) -> acc * range.count() }
                    paths.add(path to pathCombinations)
                    return
                }
                node.children().forEach { child ->
                    traverse(child, path + child.value)
                }
            }
            traverse(root, listOf(root.value))
            return paths
        }
    }

    fun createNode(rule: Rule): Day19TreeNode {
        return when (val result = rule.getRuleResult()) {
            is RuleResult.MoveTo -> {
                Day19TreeNode(NodeInfo(result.target, rule.conditionOperation()))
            }

            is RuleResult.Accepted -> {
                Day19TreeNode(NodeInfo("A", rule.conditionOperation()))
            }

            is RuleResult.Rejected -> {
                Day19TreeNode(NodeInfo("R", rule.conditionOperation()))
            }

            else -> error("Invalid result: $result")
        }
    }

    fun readTree(input: List<String>): Day19Tree {
        val workflows = parse(input).workflows
        val root = Day19TreeNode(NodeInfo("in", ranges = "xmas".associateWith { (1..4000) }))
        val tree = Day19Tree(root)
        val queue = mutableListOf<Day19TreeNode>()
        queue.add(root)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val rules = workflows[current.value.label] ?: continue
            listOf(
                rules.getOrNull(0),
                rules.getOrNull(1),
                rules.getOrNull(2),
                rules.getOrNull(3)
            ).forEachIndexed { index, rule ->
                if (rule != null) {
                    val node = createNode(rule).apply {
                        (0 until index).forEach { i ->
                            value.addFalseCondition(rules[i].conditionOperation())
                        }
                        value.updateValidRanges(current.value.ranges)
                    }
                    current.addChild(node)
                    queue.add(node)
                }
            }
        }
        return tree
    }

    fun solve(input: List<String>): Long {
        val tree = readTree(input)
        return tree.allAcceptedPaths().sumOf { it.second }
    }


    val testInput = readInput("day19_sample", "day19")
    check(solve(testInput) == 167409079868000L)
    val input = readInput("day19_input", "day19")
    measureTimedValue { solve(input) }
        .also { println("Part1 response: ${it.value} took ${it.duration}") }
}