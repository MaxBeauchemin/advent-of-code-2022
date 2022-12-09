import java.lang.Math.abs

enum class Direction(val key: String, val xChange: Int, val yChange: Int) {
    UP("U", 0, 1),
    DOWN("D", 0, -1),
    LEFT("L", -1, 0),
    RIGHT("R", 1, 0);

    companion object {
        val keys = Direction.values().associateBy { it.key }
    }
}

data class Position(var x: Int, var y: Int) {
    fun key() = "$x $y"

    fun apply(direction: Direction) {
        this.x += direction.xChange
        this.y += direction.yChange
    }

    fun follow(other: Position) {
        val xDiff = other.x - this.x
        val yDiff = other.y - this.y

        val absXDiff = abs(xDiff)
        val absYDiff = abs(yDiff)

        val xChange = if (xDiff > 0) 1 else -1
        val yChange = if (yDiff > 0) 1 else -1

        // If the head is ever two steps directly up, down, left, or right from the tail,
        // the tail must also move one step in that direction so it remains close enough
        if ((absXDiff == 2 && yDiff == 0) || (xDiff == 0 && absYDiff == 2)) {
            if (absXDiff > 0) {
                this.x += xChange
            } else {
                this.y += yChange
            }
            return
        }

        // Otherwise, if the head and tail aren't touching and aren't in the same row or column,
        // the tail always moves one step diagonally to keep up
        if ((absXDiff >= 1 && absYDiff >= 1) && (absXDiff > 1 || absYDiff > 1)) {
            this.x += xChange
            this.y += yChange
        }
    }
}

fun main() {
    fun parseInstructions(input: List<String>): List<Direction> {
        return input.flatMap {
            it.split(" ").let { tokens ->
                val direction = Direction.keys[tokens[0]]!!

                (1..tokens[1].toInt()).map {
                    direction
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val instructions = parseInstructions(input)
        val head = Position(1, 1)
        val tail = Position(1, 1)
        val visited = mutableListOf(tail.key())
        instructions.forEach { dir ->
            head.apply(dir)
            tail.follow(head)
            visited.add(tail.key())
        }
        return visited.distinct().size
    }

    fun part2(input: List<String>): Int {
        val instructions = parseInstructions(input)
        val knots = (1..10).map {
            Position(1, 1)
        }
        val visited = mutableListOf(knots.last().key())
        instructions.forEach { dir ->
            knots[0].apply(dir)
            for (i in 1..9) {
                knots[i].follow(knots[i - 1])
            }
            visited.add(knots.last().key())
        }
        return visited.distinct().size
    }

    val testInput = readInput("Day09_test")
    val secondTestInput = readInput("Day09_second_test")
    val input = readInput("Day09")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 13)
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test] : ${part2(testInput)}")
    check(part2(testInput) == 1)
    println("Part 2 [2nd Test] : ${part2(secondTestInput)}")
    check(part2(secondTestInput) == 36)
    println("Part 2 [Real] : ${part2(input)}")
}
