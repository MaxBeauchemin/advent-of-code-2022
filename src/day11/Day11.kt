package day11

import readInput
import java.lang.Exception

private fun lowestCommonDenominator(a: Long, b: Long): Long {
    val biggerNum = if (a > b) a else b
    var lcm = biggerNum
    while (true) {
        if (((lcm % a) == 0L) && ((lcm % b) == 0L)) {
            break
        }

        lcm += biggerNum
    }

    return lcm
}

data class Monkey(
    val name: String,
    val items: MutableList<Long>,
    val operationTokens: List<String>,
    var operationsCount: Long = 0,
    val testDivisor: Long,
    val trueResMonkeyName: String,
    val falseResMonkeyName: String
) {
    fun doOperationOn(item: Long): Long {
        operationsCount++

        fun itemVal(itemStr: String): Long {
            return if (itemStr == "old") item
            else itemStr.toLong()
        }

        val item1 = itemVal(operationTokens[0])
        val item2 = itemVal(operationTokens[2])

        val res = when (val operator = operationTokens[1]) {
            "*" -> item1 * item2
            "+" -> item1 + item2
            else -> throw Exception("Operator $operator Unknown")
        }

        return res
    }

    fun test(input: Long) = input % testDivisor == 0L
}

fun reduce(input: Long) = input / 3

fun main() {
    fun parseMonkeys(input: List<String>): List<Monkey> {
        return input.chunked(7).map { monkeyArr ->
            Monkey(
                name = monkeyArr[0].removePrefix("Monkey ").removeSuffix(":"),
                items = monkeyArr[1].removePrefix("  Starting items: ").let {
                    it.split(", ").map { i -> i.toLong() }.toMutableList()
                },
                operationTokens = monkeyArr[2].removePrefix("  Operation: new = ").split(" "),
                testDivisor = monkeyArr[3].removePrefix("  Test: divisible by ").toLong(),
                trueResMonkeyName = monkeyArr[4].removePrefix("    If true: throw to monkey "),
                falseResMonkeyName = monkeyArr[5].removePrefix("    If false: throw to monkey ")
            )
        }
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input)

        repeat(20) { _ ->
            monkeys.forEach { monkey ->
                monkey.items.forEach { item ->
                    var modifiedItem = item

                    modifiedItem = monkey.doOperationOn(modifiedItem)
                    modifiedItem = reduce(modifiedItem)
                    monkey.test(modifiedItem).also {
                        (if (it) monkey.trueResMonkeyName else monkey.falseResMonkeyName).also { name ->
                            monkeys.find { it.name == name }!!.also { targetMonkey ->
                                targetMonkey.items.add(modifiedItem)
                            }
                        }
                    }
                }
                monkey.items.clear()
            }
        }

        return monkeys.sortedByDescending { it.operationsCount }.take(2).let {
            it[0].operationsCount * it[1].operationsCount
        }
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input)

        val divisors = monkeys.map { it.testDivisor }.distinct()

        val lcd = divisors.fold(1L, fun (acc, i) = lowestCommonDenominator(acc, i))

        repeat(10000) { _ ->
            monkeys.forEach { monkey ->
                monkey.items.forEach { item ->
                    var modifiedItem = item

                    modifiedItem = monkey.doOperationOn(modifiedItem)

                    modifiedItem = modifiedItem % lcd

                    monkey.test(modifiedItem).also {
                        (if (it) monkey.trueResMonkeyName else monkey.falseResMonkeyName).also { name ->
                            monkeys.find { it.name == name }!!.also { targetMonkey ->
                                targetMonkey.items.add(modifiedItem)
                            }
                        }
                    }
                }
                monkey.items.clear()
            }
        }

        return monkeys.sortedByDescending { it.operationsCount }.take(2).let {
            it[0].operationsCount * it[1].operationsCount
        }
    }

    val testInput = readInput("Day11_test")
    val input = readInput("Day11")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 10605L)
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test] : ${part2(testInput)}")
    check(part2(testInput) == 2713310158)
    println("Part 2 [Real] : ${part2(input)}")
}
