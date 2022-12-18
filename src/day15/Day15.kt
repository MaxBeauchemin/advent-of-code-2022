package day15

import readInput
import java.math.BigInteger
import kotlin.math.abs

data class Coordinate(val x: Int, val y: Int) {
    fun distanceFrom(other: Coordinate) = abs(this.x - other.x) + abs(this.y - other.y)
    fun upRight() = Coordinate(x + 1, y - 1)
    fun upLeft() = Coordinate(x - 1, y - 1)
    fun downRight() = Coordinate(x + 1, y + 1)
    fun downLeft() = Coordinate(x - 1, y + 1)
}

val inputRegex = """Sensor at x=([-0-9]+), y=([-0-9]+): closest beacon is at x=([-0-9]+), y=([-0-9]+)""".toRegex()

data class Sensor(
    val location: Coordinate,
    val nearestBeacon: Coordinate
) {
    val distanceToNearestBeacon = location.distanceFrom(nearestBeacon)
    fun outsideLeft() = Coordinate(location.x - (distanceToNearestBeacon + 1), location.y)
    fun outsideRight() = Coordinate(location.x + (distanceToNearestBeacon + 1), location.y)
    fun outsideUp() = Coordinate(location.x, location.y - (distanceToNearestBeacon + 1))
    fun outsideDown() = Coordinate(location.x, location.y + (distanceToNearestBeacon + 1))
}

fun parse(input: List<String>): List<Sensor> {
    return input.mapNotNull { str ->
        inputRegex.find(str)?.let { matchResult ->
            val (x1, y1, x2, y2) = matchResult.destructured

            Sensor(
                location = Coordinate(x1.toInt(), y1.toInt()),
                nearestBeacon = Coordinate(x2.toInt(), y2.toInt())
            )
        }
    }
}

fun main() {
    fun part1(input: List<String>, row: Int): Int {
        val sensors = parse(input)

        var emptyCount = 0

        val startX = sensors.minOf { it.location.x - it.distanceToNearestBeacon }
        val endX = sensors.maxOf { it.location.x + it.distanceToNearestBeacon }

        for (x in startX..endX) {
            Coordinate(x, row).also { spot ->
                sensors.any { sensor ->
                    spot != sensor.nearestBeacon && sensor.location.distanceFrom(spot) <= sensor.distanceToNearestBeacon
                }.also { empty ->
                    if (empty) emptyCount++
                }
            }
        }

        return emptyCount
    }

    fun part2(input: List<String>, maxRange: Int): BigInteger {
        val sensors = parse(input)

        fun calc(coordinate: Coordinate): BigInteger{
            return (BigInteger.valueOf(coordinate.x.toLong()) * BigInteger.valueOf(4000000L)) + BigInteger.valueOf(coordinate.y.toLong())
        }

        fun foundIt(toCheckAgainst: List<Sensor>, coordinate: Coordinate, maxRange: Int): Boolean {
            (0..maxRange).let {
                if (coordinate.x !in it) return false
                if (coordinate.y !in it) return false
            }

            return toCheckAgainst.all { sensor ->
                sensor.distanceToNearestBeacon < sensor.location.distanceFrom(coordinate) //coordinate != sensor.nearestBeacon &&
            }
        }

        sensors.forEach { sensor ->

            val toCheckAgainst = sensors.minus(sensor)

            val left = sensor.outsideLeft()
            val up = sensor.outsideUp()
            val right = sensor.outsideRight()
            val down = sensor.outsideDown()

            var currSpot = left
            while (currSpot != up) {
                if (foundIt(toCheckAgainst, currSpot, maxRange)) {
                    return calc(currSpot)
                }
                currSpot = currSpot.upRight()
            }

            while (currSpot != right) {
                if (foundIt(toCheckAgainst, currSpot, maxRange)) {
                    return calc(currSpot)
                }
                currSpot = currSpot.downRight()
            }

            while (currSpot != down) {
                if (foundIt(toCheckAgainst, currSpot, maxRange)) {
                    return calc(currSpot)
                }
                currSpot = currSpot.downLeft()
            }

            while (currSpot != left) {
                if (foundIt(toCheckAgainst, currSpot, maxRange)) {
                    return calc(currSpot)
                }
                currSpot = currSpot.upLeft()
            }
        }

        return BigInteger.ZERO
    }

    val testInput = readInput("Day15_test")
    val input = readInput("Day15")

    part1(testInput, 10).also {
        println("Part 1 [Test] : $it")
        check(it == 26)
    }

    println("Part 1 [Real] : ${part1(input, 2000000)}")

    part2(testInput, 20).also {
        println("Part 2 [Test] : $it")
        check(it == BigInteger.valueOf(56000011L))
    }

    println("Part 2 [Real] : ${part2(input, 4000000)}")
}
