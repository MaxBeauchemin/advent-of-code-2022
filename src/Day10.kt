import java.lang.Math.abs

enum class CMDType {
    NOOP,
    ADDX
}

data class CMD(val type: CMDType, val arg: Int? = null)

fun List<Boolean>.render() {
    this.chunked(40).forEach { row ->
        row.map { bool ->
            if (bool) "#" else "."
        }.also {
            println(it.joinToString(""))
        }
    }
}

fun main() {
    fun parseInput(input: List<String>): List<CMD> {
        return input.map { str ->
            str.split(" ").let { tokens ->
                if (tokens[0] == "noop") CMD(CMDType.NOOP)
                else CMD(CMDType.ADDX, tokens[1].toInt())
            }
        }
    }

    fun part1(input: List<String>): Int {
        var clock = 1
        var xReg = 1
        var prevStop = -20
        var signalSum = 0
        val commands = parseInput(input).toMutableList()

        var addInProg = false
        var addNext = 0

        while (commands.any() || addInProg) {
            if (prevStop + 40 == clock) {
                prevStop = clock
                signalSum += (xReg * clock)
            }

            if (addInProg) {
                xReg += addNext
                addInProg = false
            } else {
                val cmd = commands.first()
                commands.removeAt(0)
                if (cmd.type == CMDType.ADDX) {
                    addInProg = true
                    addNext = cmd.arg!!
                }
            }

            clock++
        }

        return signalSum
    }

    fun part2(input: List<String>): List<Boolean> {
        var clock = 1
        var xReg = 1
        val commands = parseInput(input).toMutableList()
        val pixels = mutableListOf<Boolean>()

        val xHistory = mutableListOf<Int>()

        var addInProg = false
        var addNext = 0

        while (commands.any() || addInProg) {
            xHistory.add(xReg)

            val pixelLit = abs(xReg - (clock - 1)) <= 1
            pixels.add(pixelLit)

            if (addInProg) {
                xReg += addNext
                addInProg = false
            } else {
                val cmd = commands.first()
                commands.removeAt(0)
                if (cmd.type == CMDType.ADDX) {
                    addInProg = true
                    addNext = cmd.arg!!
                }
            }

            if (clock == 40) {
                clock = 1
            } else clock++
        }

        return pixels
    }

    val testInput = readInput("Day10_test")
    val input = readInput("Day10")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 13140)
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test]")
    part2(testInput).render()
    println()
    println("Part 2 [Real]")
    part2(input).render()
}
