package day03

import readInput
import kotlin.math.ceil

fun main() {
    fun charPriority(char: Char): Int {
        return when (char.code) {
            in 97..122 -> {
                char.code - 96 // a - z
            }
            in 65..90 -> {
                char.code - 38 // A - Z
            }
            else -> 0
        }
    }

    fun groupPriority(input: List<Set<Char>>): Int {
        val first = input[0]
        val withoutFirst = input.subList(1, input.size)

        val shared = withoutFirst.fold(first) { acc, set ->
            acc.intersect(set)
        }

        return charPriority(shared.first())
    }

    fun rowPriority(input: String): Int {
        val halfSize = ceil(input.length.toFloat() / 2).toInt()

        return groupPriority(input.chunked(halfSize).map { it.toSet() })
    }

    fun part1(input: List<String>): Int {
        return input.sumOf {
            rowPriority(it)
        }
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3).sumOf { chunk ->
            groupPriority(chunk.map { it.toSet() })
        }
    }

    val testInput = readInput("Day03_test")
    val input = readInput("Day03")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 157)
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test] : ${part2(testInput)}")
    check(part2(testInput) == 70)
    println("Part 2 [Real] : ${part2(input)}")
}
