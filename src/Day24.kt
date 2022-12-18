fun main() {
    fun part1(input: List<String>): Int {
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day24_test")
    val input = readInput("Day24")

    part1(testInput).also {
        println("Part 1 [Test] : $it")
        check(it == 0)
    }

    println("Part 1 [Real] : ${part1(input)}")

    part2(testInput).also {
        println("Part 2 [Test] : $it")
        check(it == 0)
    }

    println("Part 2 [Real] : ${part2(input)}")
}
