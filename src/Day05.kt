import kotlin.math.max
import kotlin.math.min

fun main() {

    val mappingRegex = "(\\w+)-to-(\\w+) map:".toRegex()
    val numberRegex = "\\d+".toRegex()

    data class MappingData(
        val dest: Long,
        val source: Long,
        val length: Long,
    ) {
        val sourceRange = source until source + length
        val destOffset = dest - source
    }

    data class Mapping(
        val sourceName: String,
        val destName: String,
        val mappingDataList: List<MappingData>,
    )

    data class Almanac(
        val seeds: List<Long>,
        val mappings: List<Mapping>,
    )

    fun LongRange.inBetween(range: LongRange): Boolean {
        return !(range.last < first || last < range.first)
    }

    fun parse(input: List<String>): Almanac {
        val seeds = numberRegex.findAll(input[0]).map { it.value.toLong() }.toList()
        val mappings = input
            .drop(2)
            .mapUntilEmpty {
                val (sourceName, destinyName) = mappingRegex.find(it[0])!!.destructured
                val mappingEntries = it.drop(1).map { line ->
                    val data = "\\d+".toRegex().findAll(line).map { it.value.toLong() }.toList()
                    MappingData(data[0], data[1], data[2])
                }
                Mapping(sourceName, destinyName, mappingEntries)
            }

        return Almanac(seeds, mappings)
    }

    tailrec fun processMappings(ranges: List<LongRange>, mappings: List<Mapping>): List<LongRange> {
        if (mappings.isEmpty()) return ranges
        val nextMapping = mappings
            .first()
            .mappingDataList
            .sortedBy { it.source }
        val mappedRanges = ranges.flatMap { seedRange ->
            val newRanges = mutableListOf<LongRange>()
            var current = seedRange
            for (mappingEntry in nextMapping) {
                if (current.isEmpty()) break //no more ranges to map
                if (!current.inBetween(mappingEntry.sourceRange)) {
                    //we can't map this range with the current mapping entry, continue to the next one
                    continue
                } else {
                    val commonStart = max(current.first, mappingEntry.sourceRange.first)
                    val commonEnd = min(current.last, mappingEntry.sourceRange.last)
                    val leftRange = current.first until commonStart
                    val commonRange = commonStart + mappingEntry.destOffset..commonEnd + mappingEntry.destOffset
                    val rightRange = commonEnd + 1..current.last
                    newRanges.add(leftRange)
                    newRanges.add(commonRange)
                    current = rightRange
                }
            }
            if (!current.isEmpty()) newRanges.plusElement(current) else newRanges
        }

        return processMappings(mappedRanges, mappings.drop(1))
    }

    fun part1(input: List<String>): Long {
        val almanac = parse(input)
        return almanac.seeds.minOf { seed ->
            val range = seed..seed
            processMappings(listOf(range), almanac.mappings).minOf { it.first }
        }
    }

    fun part2(input: List<String>): Long {
        val almanac = parse(input)
        val ranges = almanac.seeds.chunked(2).map { (rangeStart, length) ->
            rangeStart until rangeStart + length
        }
        return processMappings(ranges, almanac.mappings)
            .minOf { it.first }
    }

    val testInput = readInput("day5_sample", "day5")

    check(part1(testInput).also { println(it) } == 35L)
    check(part2(testInput).also { println(it) } == 46L)
    val input = readInput("day5_input", "day5")
    part1(input).println()
    part2(input).println()
}
