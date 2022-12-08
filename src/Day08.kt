import kotlin.math.abs

fun main() {
    fun parseInput(input: List<String>): List<List<Int>> {
        return input.map { str ->
            str.toList().map { char ->
                char.digitToInt()
            }
        }
    }

    fun List<Int>.allSmallerThan(compare: Int) = this.all { it < compare }

    fun List<List<Int>>.checkVisible(outerIdx: Int, innerIdx: Int): Boolean {
        val outerLastIdx = this.size - 1
        val innerLastIdx = this[outerIdx].size - 1

        val height = this[outerIdx][innerIdx]

        //Edge Check
        if (outerIdx == 0 || innerIdx == 0 || outerIdx == outerLastIdx || innerIdx == innerLastIdx) return true

        //Inner Checks
        val innerBefore = this[outerIdx].subList(0, innerIdx)

        if (innerBefore.allSmallerThan(height)) return true

        val innerAfter = this[outerIdx].slice(innerIdx + 1..innerLastIdx)

        if (innerAfter.allSmallerThan(height)) return true

        //Outer Checks
        val outerBefore = this.subList(0, outerIdx).map { it[innerIdx] }

        if (outerBefore.allSmallerThan(height)) return true

        val outerAfter = this.slice(outerIdx + 1..outerLastIdx).map { it[innerIdx] }

        if (outerAfter.allSmallerThan(height)) return true

        return false
    }

    fun List<List<Int>>.scenicScore(outerIdx: Int, innerIdx: Int): Int {
        val outerLastIdx = this.size - 1
        val innerLastIdx = this[outerIdx].size - 1

        val height = this[outerIdx][innerIdx]

        val outerBeforeScore = if (outerIdx == 0) 0 else {
            var lastVisibleIdxAfter = -1
            for (o in outerIdx - 1 downTo 0) {
                if (lastVisibleIdxAfter == -1 && this[o][innerIdx] >= height) lastVisibleIdxAfter = o
            }

            if (lastVisibleIdxAfter == -1) outerIdx else {
                abs(lastVisibleIdxAfter - outerIdx)
            }
        }

        val outerAfterScore = if (outerIdx == outerLastIdx) 0 else {
            var lastVisibleIdxAfter = -1

            for (o in outerIdx + 1..outerLastIdx) {
                if (lastVisibleIdxAfter == -1 && this[o][innerIdx] >= height) lastVisibleIdxAfter = o
            }

            if (lastVisibleIdxAfter == -1) outerLastIdx - outerIdx else {
                abs(lastVisibleIdxAfter - outerIdx)
            }
        }

        val innerBeforeScore = if (innerIdx == 0) 0 else {
            var lastVisibleIdxBefore = -1

            for (i in innerIdx - 1 downTo 0) {
                if (lastVisibleIdxBefore == -1 && this[outerIdx][i] >= height) lastVisibleIdxBefore = i
            }

            if (lastVisibleIdxBefore == -1) innerIdx else {
                abs(lastVisibleIdxBefore - innerIdx)
            }
        }

        val innerAfterScore = if (innerIdx == innerLastIdx) 0 else {
            var lastVisibleIdxAfter = -1

            for (i in innerIdx + 1..innerLastIdx) {
                if (lastVisibleIdxAfter == -1 && this[outerIdx][i] >= height) lastVisibleIdxAfter = i
            }

            if (lastVisibleIdxAfter == -1) innerLastIdx - innerIdx else {
                abs(lastVisibleIdxAfter - innerIdx)
            }
        }

        return outerBeforeScore * outerAfterScore * innerBeforeScore * innerAfterScore
    }

    fun part1(input: List<String>): Int {
        return parseInput(input).let { data ->
            data.mapIndexed { outerIdx, inner ->
                inner.mapIndexed { innerIdx, _ ->
                    if (data.checkVisible(outerIdx, innerIdx)) 1 else 0
                }
            }
        }.flatten().sum()
    }

    fun part2(input: List<String>): Int {
        return parseInput(input).let { data ->
            data.mapIndexed { outerIdx, inner ->
                inner.mapIndexed { innerIdx, _ ->
                    data.scenicScore(outerIdx, innerIdx)
                }
            }
        }.flatten().max()
    }

    val testInput = readInput("Day08_test")
    val input = readInput("Day08")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 21)
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test] : ${part2(testInput)}")
    check(part2(testInput) == 8)
    println("Part 2 [Real] : ${part2(input)}")
}
