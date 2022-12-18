package day01

import readInput

fun main() {
    fun batchSumAndSort(input: List<String>): List<Int> {
        val batches = mutableListOf<List<String>>()
        val currList = mutableListOf<String>()

        input.forEach { entry ->
            if (entry == "") {
                batches.add(currList.toList())
                currList.clear()
            } else {
                currList.add(entry)
            }
        }

        return batches.map { batch ->
            batch.sumOf { entry ->
                entry.toInt()
            }
        }.sortedByDescending {
            it
        }
    }

    fun part1(input: List<String>): Int {
        return batchSumAndSort(input).first()
    }

    fun part2(input: List<String>): Int {
        return batchSumAndSort(input).filterIndexed { index, _ -> index < 3 }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    println(part1(testInput))
    check(part1(testInput) == 24000)

    val input = readInput("day01")
    println(part1(input))
    println(part2(input))
}
