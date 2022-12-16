

fun main() {
    fun part1(input: List<String>, row: Int): Int {
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day15_test")
    val input = readInput("Day15")

    println("Part 1 [Test] : ${part1(testInput, 10)}")
    check(part1(testInput, 10) == 26)
    println("Part 1 [Real] : ${part1(input, 2000000)}")

//    println("Part 2 [Test] : ${part2(testInput)}")
//    check(part2(testInput) == 93)
//    println("Part 2 [Real] : ${part2(input)}")
}
