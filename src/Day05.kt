import java.util.*

data class Instruction(
    val moveCount: Int,
    val origin: Int,
    val destination: Int
)

fun main() {
    val instructionRegex = """move ([0-9]+) from ([0-9]+) to ([0-9]+)""".toRegex()

    // 1: 1, 2: 5, 3: 9, 4: 13
    fun crateKeyToLineIndex(key: Int): Int {
        return if (key == 1) 1
        else ((key - 1) * 4) + 1
    }

    fun parseInput(input: List<String>): Pair<Map<Int, Stack<String>>, List<Instruction>> {
        val indexOfStackNames = input.indexOfFirst { it.replace(" ", "").toIntOrNull() != null }

        val crateKeys = input[indexOfStackNames].split(" ").mapNotNull { it.toIntOrNull() }

        val crateLines = input.slice(0 until indexOfStackNames)

        val crateStacks = mutableMapOf<Int, Stack<String>>()

        crateLines.reversed().map { str ->
            val charList = str.toList()
            crateKeys.forEach { key ->
                val idx = crateKeyToLineIndex(key)

                if (idx < charList.size) {
                    val crateContent = charList[idx].toString()

                    if (crateContent.isNotBlank()) {
                        if (crateStacks.containsKey(key)) {
                            crateStacks[key]!!.push(crateContent)
                        } else {
                            crateStacks[key] = Stack<String>().also { it.push(crateContent) }
                        }
                    }
                }
            }
        }

        val instructionLines = input.slice(indexOfStackNames + 1 until input.size)

        val instructions = instructionLines.mapNotNull { str ->
            instructionRegex.find(str)?.let { matchResult ->
                val (moveCount, origin, destination) = matchResult.destructured

                Instruction(
                    moveCount.toInt(),
                    origin.toInt(),
                    destination.toInt()
                )
            }
        }

        return crateStacks.toMap() to instructions
    }

    fun part1(input: List<String>): String {
        val (crateStacks, instructions) = parseInput(input)

        instructions.forEach { i ->
            repeat(i.moveCount) {
                crateStacks[i.origin]!!.pop().also { crateContent ->
                    crateStacks[i.destination]!!.push(crateContent)
                }
            }
        }

        return crateStacks.entries.sortedBy { it.key }.joinToString("") { it.value.peek() }
    }

    fun part2(input: List<String>): String {
        val (crateStacks, instructions) = parseInput(input)

        // ugly but simple
        val tempStack = Stack<String>()

        instructions.forEach { i ->
            repeat(i.moveCount) {
                crateStacks[i.origin]!!.pop().also { crateContent ->
                    tempStack.push(crateContent)
                }
            }
            repeat(i.moveCount) {
                crateStacks[i.destination]!!.push(tempStack.pop())
            }
        }

        return crateStacks.entries.sortedBy { it.key }.joinToString("") { it.value.peek() }
    }

    val testInput = readInput("Day05_test")
    val input = readInput("Day05")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == "CMZ")
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test] : ${part2(testInput)}")
    check(part2(testInput) == "MCD")
    println("Part 2 [Real] : ${part2(input)}")
}
