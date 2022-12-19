package day17

import readInput
import java.lang.Exception

var skipDebugConsole = true

data class Coordinate(val x: Int, val y: Int) {
    val key = "$x $y"
    fun left() = Coordinate(x - 1, y)
    fun right() = Coordinate(x + 1, y)
    fun down() = Coordinate(x, y - 1)

    fun canMoveLeft(occupied: Set<String>): Boolean {
        return left().let {
            it.x >= 0 && !occupied.contains(it.key)
        }
    }

    fun canMoveRight(occupied: Set<String>, maxX: Int): Boolean {
        return right().let {
            it.x <= maxX && !occupied.contains(it.key)
        }
    }

    fun canMoveDown(occupied: Set<String>): Boolean {
        return down().let {
            it.y >= 0 && !occupied.contains(it.key)
        }
    }
}

data class Shape(
    var spots: Set<Coordinate>
) {
    fun canMoveLeft(occupied: Set<String>) = spots.all { it.canMoveLeft(occupied) }
    fun canMoveRight(occupied: Set<String>, maxX: Int) = spots.all { it.canMoveRight(occupied, maxX) }
    fun canMoveDown(occupied: Set<String>) = spots.all { it.canMoveDown(occupied) }

    fun moveLeft() {
        spots = spots.map { it.left() }.toSet()
    }
    fun moveRight() {
        spots = spots.map { it.right() }.toSet()
    }
    fun moveDown() {
        spots = spots.map { it.down() }.toSet()
    }
}

fun Set<Shape>.occupiedSpots() = this.flatMap { it.spots.map { s -> s.key } }.toSet()

fun Set<Shape>.draw(bottomY: Int, maxX: Int, currShape: Shape) {
    if (skipDebugConsole) return

    val occupied = this.occupiedSpots()

    println()
    println()

    for (y in bottomY downTo 0) {
        var str = ""
        for (x in 0..maxX) {
            str += if (occupied.contains("$x $y")) '#' else if (currShape.spots.any { it.key == "$x $y" }) '@' else '.'
        }
        println("${y.toString().padStart(3, ' ')} |$str|")
    }

    val line = "".padStart(maxX + 1, '-')
    println("    +$line+")


    Thread.sleep(600)
}

fun log(msg: String) {
    if (skipDebugConsole) return

    println(msg)
}

enum class PushDirection{
    LEFT,
    RIGHT
}

fun main() {
    var currShapeIndex = 0
    var currInstructionIndex = 0

    fun parsePushes(input: String): List<PushDirection> {
        return input.toList().map { char ->
            when (char) {
                '>' -> PushDirection.RIGHT
                '<' -> PushDirection.LEFT
                else -> throw Exception("Invalid Input")
            }
        }
    }

    fun getNextPushDirection(directions: List<PushDirection>): PushDirection {
        val direction = directions[currInstructionIndex]

        currInstructionIndex = (currInstructionIndex + 1) % directions.size

        return direction
    }

    fun getNextShape(highestY: Int): Shape {
        val bottomY = highestY + 4

        val spots = when (currShapeIndex) {
            0 -> {
                setOf(
                    Coordinate(2, bottomY),
                    Coordinate(3, bottomY),
                    Coordinate(4, bottomY),
                    Coordinate(5, bottomY),
                )
            }
            1 -> {
                setOf(
                    Coordinate(2, bottomY + 1),
                    Coordinate(3, bottomY),
                    Coordinate(3, bottomY + 1),
                    Coordinate(3, bottomY + 2),
                    Coordinate(4, bottomY + 1)
                )
            }
            2 -> {
                setOf(
                    Coordinate(2, bottomY),
                    Coordinate(3, bottomY),
                    Coordinate(4, bottomY),
                    Coordinate(4, bottomY + 1),
                    Coordinate(4, bottomY + 2)
                )
            }
            3 -> {
                setOf(
                    Coordinate(2, bottomY),
                    Coordinate(2, bottomY + 1),
                    Coordinate(2, bottomY + 2),
                    Coordinate(2, bottomY + 3),
                )
            }
            4 -> {
                setOf(
                    Coordinate(2, bottomY),
                    Coordinate(2, bottomY + 1),
                    Coordinate(3, bottomY),
                    Coordinate(3, bottomY + 1),
                )
            }
            else -> {
                throw Exception("Invalid Shape Index")
            }
        }

        currShapeIndex = (currShapeIndex + 1) % 5

        return Shape(spots)
    }

    fun part1(input: List<String>, stop: Int): Int {
        currShapeIndex = 0
        currInstructionIndex = 0

        val directions = parsePushes(input.first())

        val settledShapes = mutableSetOf<Shape>()

        var bottomY = -1

        while (settledShapes.size < stop) {
            val occupied = settledShapes.occupiedSpots()

            val shape = getNextShape(bottomY)

            log("Shape ${settledShapes.size + 1} has started falling")
            settledShapes.draw(bottomY + 5, 6, shape)

            var settled = false

            while (!settled) {

                when (getNextPushDirection(directions)) {
                    PushDirection.LEFT -> {
                        if (shape.canMoveLeft(occupied)){
                            shape.moveLeft()
                            log("Shape pushed to the Left")
                        }
                        else {
                            log("Shape couldn't be moved Left")
                        }
                    }
                    PushDirection.RIGHT -> {
                        if (shape.canMoveRight(occupied, 6)) {
                            shape.moveRight()
                            log("Shape pushed to the Right")
                        }
                        else {
                            log("Shape couldn't be moved Right")
                        }
                    }
                }

                settledShapes.draw(bottomY + 5, 6, shape)

                if (shape.canMoveDown(occupied)) {
                    shape.moveDown()

                    log("Shape moved Down")
                } else {
                    settled = true

                    log("Shape has settled")
                }

                settledShapes.draw(bottomY + 5, 6, shape)
            }

            bottomY = maxOf(bottomY, shape.spots.maxOf { it.y })

            settledShapes.add(shape)
        }

        return bottomY + 1
    }

    fun part2(input: List<String>, stop: Int): Int {
        currShapeIndex = 0
        currInstructionIndex = 0

        val directions = parsePushes(input.first())

        val settledShapes = mutableSetOf<Shape>()

        var bottomY = -1

        while (settledShapes.size < stop) {
            val occupied = settledShapes.occupiedSpots()

            val shape = getNextShape(bottomY)

            log("Shape ${settledShapes.size + 1} has started falling")
            settledShapes.draw(bottomY + 5, 6, shape)

            var settled = false

            while (!settled) {

                when (getNextPushDirection(directions)) {
                    PushDirection.LEFT -> {
                        if (shape.canMoveLeft(occupied)){
                            shape.moveLeft()
                            log("Shape pushed to the Left")
                        }
                        else {
                            log("Shape couldn't be moved Left")
                        }
                    }
                    PushDirection.RIGHT -> {
                        if (shape.canMoveRight(occupied, 6)) {
                            shape.moveRight()
                            log("Shape pushed to the Right")
                        }
                        else {
                            log("Shape couldn't be moved Right")
                        }
                    }
                }

                settledShapes.draw(bottomY + 5, 6, shape)

                if (shape.canMoveDown(occupied)) {
                    shape.moveDown()

                    log("Shape moved Down")
                } else {
                    settled = true

                    log("Shape has settled")
                }

                settledShapes.draw(bottomY + 5, 6, shape)
            }

            bottomY = maxOf(bottomY, shape.spots.maxOf { it.y })

            settledShapes.add(shape)
        }

        return bottomY + 1
    }

    val testInput = readInput("Day17_test")
    val input = readInput("Day17")

    part1(testInput, 2022).also {
        println("Part 1 [Test] : $it")
        check(it == 3068)
    }

    println("Part 1 [Real] : ${part1(input, 2022)}")

//    part2(testInput, 1000000000000L).also {
//        println("Part 2 [Test] : $it")
//        check(it == 0)
//    }
//
//    println("Part 2 [Real] : ${part2(input, 1000000000000L)}")
}
