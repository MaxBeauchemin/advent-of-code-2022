package day06

import readInput

fun main() {
    fun findUniqueBufferStartIndex(input: String, desiredSize: Int): Int {
        var buffer = ""

        input.toList().forEachIndexed { index, c ->
            buffer += c

            if (buffer.length == desiredSize + 1) {
                buffer = buffer.slice(1..desiredSize)
            }
            if (buffer.length == desiredSize) {
                if (buffer.toList().distinct().size == desiredSize) return index
            }
        }

        return Int.MIN_VALUE
    }

    fun part1(input: String): Int {
        return findUniqueBufferStartIndex(input, 4) + 1
    }

    fun part2(input: String): Int {
        return findUniqueBufferStartIndex(input, 14) + 1
    }

    val testInput = readInput("Day06_test").first()
    val input = readInput("Day06").first()

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 7)
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test] : ${part2(testInput)}")
    check(part2(testInput) == 19)
    println("Part 2 [Real] : ${part2(input)}")
}
