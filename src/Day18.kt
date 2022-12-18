data class ThreeDCoordinate(
    val x: Int,
    val y: Int,
    val z: Int
) {
    val key = "$x $y $z"

    fun surrounding(): List<ThreeDCoordinate> {
        return listOf(
            ThreeDCoordinate(x = this.x + 1, y = this.y, z = this.z),
            ThreeDCoordinate(x = this.x - 1, y = this.y, z = this.z),
            ThreeDCoordinate(x = this.x, y = this.y + 1, z = this.z),
            ThreeDCoordinate(x = this.x, y = this.y - 1, z = this.z),
            ThreeDCoordinate(x = this.x, y = this.y, z = this.z + 1),
            ThreeDCoordinate(x = this.x, y = this.y, z = this.z - 1)
        )
    }
}

fun parseCoordinates(input: List<String>): List<ThreeDCoordinate> {
    return input.map { row ->
        row.split(",").let { tokens ->
            ThreeDCoordinate(tokens[0].toInt(), tokens[1].toInt(), tokens[2].toInt())
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val coordinates = parseCoordinates(input)

        val occupied = mutableSetOf<String>()

        var surfaceArea = 0

        coordinates.forEach { coord ->
            surfaceArea += 6

            coord.surrounding().forEach { s ->
                if (occupied.contains(s.key)) surfaceArea -= 2
            }

            occupied.add(coord.key)
        }

        return surfaceArea
    }

    fun part2(input: List<String>): Int {
        val coordinates = parseCoordinates(input)

        val xDiff = coordinates.maxOf { it.x } - coordinates.minOf { it.x }
        val yDiff = coordinates.maxOf { it.y } - coordinates.minOf { it.y }
        val zDiff = coordinates.maxOf { it.z } - coordinates.minOf { it.z }

        println("total area dimension: $xDiff x $yDiff x $zDiff")

        return 0
    }

    val testInput = readInput("Day18_test")
    val input = readInput("Day18")

    part1(testInput).also {
        println("Part 1 [Test] : $it")
        check(it == 64)
    }

    println("Part 1 [Real] : ${part1(input)}")

    part2(testInput).also {
        println("Part 2 [Test] : $it")
        check(it == 58)
    }

    println("Part 2 [Real] : ${part2(input)}")
}
