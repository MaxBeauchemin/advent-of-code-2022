package day04

import readInput

fun main() {

    fun IntRange.fullyContains(other: IntRange) = this.first <= other.first && this.last >= other.last

    fun IntRange.overlaps(other: IntRange): Boolean {
        return this.first in other || this.last in other || other.first in this || other.last in this
    }

    fun toRange(txt: String): IntRange {
        return txt.split("-").let {
            it.first().toInt()..it.last().toInt()
        }
    }

    fun parseRanges(input: String): Pair<IntRange, IntRange> {
        return input.split(",").let {
            toRange(it.first()) to toRange(it.last())
        }
    }

    fun part1(input: List<String>): Int {
        return input.filter {
            parseRanges(it).let { (firstRange, secondRange) ->
                firstRange.fullyContains(secondRange) || secondRange.fullyContains(firstRange)
            }
        }.size
    }

    fun part2(input: List<String>): Int {
        return input.filter {
            parseRanges(it).let { (firstRange, secondRange) ->
                firstRange.overlaps(secondRange)
            }
        }.size
    }

    val testInput = readInput("Day04_test")
    val input = readInput("Day04")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 2)
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test] : ${part2(testInput)}")
    check(part2(testInput) == 4)
    println("Part 2 [Real] : ${part2(input)}")
}
