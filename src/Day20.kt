import kotlin.time.measureTimedValue

enum class Pulse {
    LOW, HIGH
}

enum class FlipFlopState {
    ON, OFF
}

data class PulseEvent(
    val pulse: Pulse,
    val module: String,
    val source: String = "",
    val pulsesState: MutableMap<String, Pulse>
) {
    override fun toString(): String {
        return "$source -$pulse -> $module"
    }
}

sealed interface Module {
    val name: String
    fun handlePulseEvent(process: PulseEvent): List<PulseEvent> = emptyList()
    fun getModuleOutputs(): List<String> {
        return when (this) {
            is BroadcasterModule -> outputs
            is FlipFlopModule -> outputs
            is ConjunctionModule -> outputs
            else -> emptyList()
        }
    }
}

data class BroadcasterModule(override val name: String, val outputs: MutableList<String>) : Module {

    override fun handlePulseEvent(process: PulseEvent): List<PulseEvent> {
        return outputs.map { PulseEvent(process.pulse, it, this.name, process.pulsesState) }
    }

    override fun toString(): String {
        return "BroadcasterModule(outputs=${outputs.joinToString(", ")})"
    }
}

data class FlipFlopModule(
    override val name: String,
    val outputs: List<String>,
    private var state: FlipFlopState
) : Module {

    override fun handlePulseEvent(process: PulseEvent): List<PulseEvent> {
        if (process.pulse == Pulse.HIGH) {
            return emptyList()
        }
        return if (state == FlipFlopState.OFF) {
            state = FlipFlopState.ON
            outputs.map { PulseEvent(Pulse.HIGH, it, this.name, process.pulsesState) }
        } else {
            state = FlipFlopState.OFF
            outputs.map { PulseEvent(process.pulse, it, this.name, process.pulsesState) }
        }
    }

    override fun toString(): String {
        return "FlipFlopModule(name='$name', state=$state, outputs=${outputs.joinToString(", ")})"
    }
}

data class ConjunctionModule(
    override val name: String,
    val inputs: MutableList<String>,
    val outputs: List<String>
) : Module {
    override fun handlePulseEvent(process: PulseEvent): List<PulseEvent> {
        process.pulsesState[process.source] = process.pulse
        val inputsState = process.pulsesState.filterKeys { it in inputs }
        return if (inputsState.values.all { it == Pulse.HIGH }) {
            outputs.map { PulseEvent(Pulse.LOW, it, this.name, process.pulsesState) }
        } else {
            outputs.map { PulseEvent(Pulse.HIGH, it, this.name, process.pulsesState) }
        }
    }

    override fun toString(): String {
        return "ConjunctionModule(name='$name', inputs=${inputs.joinToString(", ")}, outputs=${outputs.joinToString(", ")})"
    }
}

data class EmptyModule(override val name: String) : Module {
    override fun toString(): String {
        return "EmptyModule(name='$name')"
    }
}

data class Circuit(
    private val broadcasterModule: BroadcasterModule,
    private val flipFlopModules: List<FlipFlopModule>,
    private val conjunctionModules: List<ConjunctionModule>
) {
    private val modules: List<Module> = listOf(broadcasterModule) + flipFlopModules + conjunctionModules
    private val modulesMap = modules.associateBy { it.name }
    private val pulseMapByModule = modules.associate { it.name to Pulse.LOW }.toMutableMap()

    fun pressButton(times: Int = 1, checkHighFor: String = "", checkHighForOp: (PulseEvent) -> Unit = {}): Long {
        var lowPulseCount = 0L
        var highPulseCount = 0L
        for (it in 1..times) {
            val queue = mutableListOf(PulseEvent(Pulse.LOW, broadcasterModule.name, "button", pulseMapByModule))
            while (queue.isNotEmpty()) {
                val process = queue.removeFirst()
                val target = modulesMap[process.module] ?: EmptyModule(process.module)
                if (process.pulse == Pulse.LOW) {
                    lowPulseCount++
                } else {
                    highPulseCount++
                }
                if (checkHighFor.isNotEmpty()) {
                    if (process.pulse == Pulse.HIGH && process.module == checkHighFor) {
                        checkHighForOp(process)
                    }
                }
                val nextProcess = target.handlePulseEvent(process)
                if (nextProcess.isNotEmpty()) queue.addAll(nextProcess)
            }
        }
        return lowPulseCount * highPulseCount
    }

    fun findExitMoment(): Long {
        val rxModules = modules.find { it.name == "rx" } ?: modules.single { "rx" in it.getModuleOutputs() }
        var pressedButtonTimes = 0L
        if (rxModules is ConjunctionModule) {
            val rxInputs = rxModules.inputs
            val pendingToSee = rxInputs.toMutableList()
            val cyclesPerInput = mutableMapOf<String, Long>()
            while (true) {
                ++pressedButtonTimes
                pressButton(1, rxModules.name) {
                    if (it.source !in cyclesPerInput) {
                        cyclesPerInput[it.source] = pressedButtonTimes
                        pendingToSee.remove(it.source)
                    }
                }
                if (pendingToSee.isEmpty()) {
                    return cyclesPerInput.values.lcm()
                }

            }
        }
        error("rx module not found or invalid")
    }

    override fun toString(): String {
        return modules.joinToString("\n")
    }
}

fun main() {

    fun parse(input: List<String>): Circuit {
        val broadcaster = BroadcasterModule("broadcaster", mutableListOf())
        val flipFlopModules = mutableMapOf<String, FlipFlopModule>()
        val conjunctionModules = mutableMapOf<String, ConjunctionModule>()
        input.forEach { line ->
            val (name, outputs) = line.split("->")
            if (name.startsWith("broadcaster")) {
                broadcaster.outputs.addAll(outputs.split(",").map(String::trim))
            }
            if (name.startsWith("%")) {
                flipFlopModules[name.drop(1).trim()] = FlipFlopModule(
                    name = name.drop(1).trim(),
                    outputs = outputs.split(",").map(String::trim),
                    state = FlipFlopState.OFF
                )
            }
            if (name.startsWith("&")) {
                conjunctionModules[name.drop(1).trim()] = ConjunctionModule(
                    name = name.drop(1).trim(),
                    inputs = mutableListOf(),
                    outputs = outputs.split(",").map(String::trim)
                )
            }
        }
        conjunctionModules.keys.forEach { name ->
            val (_, inputs, _) = conjunctionModules[name]!!
            if (broadcaster.outputs.contains(name)) {
                inputs.add("broadcaster")
            }
            flipFlopModules.forEach { (flipFlopName, data) ->
                if (data.outputs.contains(name)) {
                    inputs.add(flipFlopName)
                }
            }
            conjunctionModules.forEach { (conjName, data) ->
                if (data.outputs.contains(name)) {
                    inputs.add(conjName)
                }
            }

        }
        return Circuit(
            broadcasterModule = broadcaster,
            flipFlopModules = flipFlopModules.values.toList(),
            conjunctionModules = conjunctionModules.values.toList()
        )
    }

    fun part1(input: List<String>): Long {
        val circuit = parse(input)
        return circuit.pressButton(1000)
    }

    fun part2(input: List<String>): Long {
        val circuit = parse(input)
        return circuit.findExitMoment()
    }


    val testInput = readInput("day20_sample", "day20")
    check(part1(testInput) == 32000000L)
    val otherTestInput = readInput("day20_sample_2", "day20")
    check(part1(otherTestInput) == 11687500L)
    val input = readInput("day20_input", "day20")
    measureTimedValue {
        part1(input)
    }.also { println("Part1 response: ${it.value} took ${it.duration}") }
    measureTimedValue {
        part2(input)
    }.also { println("Part2 response: ${it.value} took ${it.duration}") }
}