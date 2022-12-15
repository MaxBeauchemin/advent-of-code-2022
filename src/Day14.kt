import java.time.Instant

data class Coordinates(
    val x: Int,
    val y: Int
) {
    fun plus(xChange: Int, yChange: Int) = Coordinates(x + xChange, y + yChange)

    fun below() = plus(0, 1)
    fun belowLeft() = plus(-1, 1)
    fun belowRight() = plus(1, 1)

    companion object {
        fun fromString(input: String): Coordinates {
            return input.split(",").let { pair ->
                Coordinates(pair[0].toInt(), pair[1].toInt())
            }
        }
    }
}

data class Sand(
    var location: Coordinates,
    var isSettled: Boolean = false
) {
    fun fall(occupied: Set<Coordinates>, infiniteFloorY: Int? = null) {
        val newCoordinates = if (!occupied.contains(this.location.below()) && (infiniteFloorY == null || this.location.below().y < infiniteFloorY)) {
            this.location.below()
        } else if (!occupied.contains(this.location.belowLeft()) && (infiniteFloorY == null || this.location.belowLeft().y < infiniteFloorY)) {
            this.location.belowLeft()
        } else if (!occupied.contains(this.location.belowRight()) && (infiniteFloorY == null || this.location.belowRight().y < infiniteFloorY)) {
            this.location.belowRight()
        } else null

        if (newCoordinates != null) {
            this.location = newCoordinates
        } else {
            this.isSettled = true
        }
    }
}

data class Block(
    val location: Coordinates
)

fun parseBlocks(input: List<String>): Set<Block> {
    var blocks = mutableSetOf<Block>()

    input.forEach { line ->
        line.split(" -> ").windowed(2) { pair ->
            val start = Coordinates.fromString(pair[0])
            val end = Coordinates.fromString(pair[1])

            if (start == end) blocks.add(Block(start))
            else {
                // move y direction
                if (start.x == end.x) {
                    val yDelta = if (start.y < end.y) 1 else -1

                    var currCoords = start

                    while (currCoords != end) {
                        blocks.add(Block(currCoords))
                        currCoords = currCoords.plus(0, yDelta)
                    }
                    blocks.add(Block(end))
                } else if (start.y == end.y) {
                    val xDelta = if (start.x < end.x) 1 else -1

                    var currCoords = start

                    while (currCoords != end) {
                        blocks.add(Block(currCoords))
                        currCoords = currCoords.plus(xDelta, 0)
                    }
                    blocks.add(Block(end))
                } else {
                    throw Exception("Diagonals Invalid")
                }
            }
        }
    }

    return blocks
}

fun main() {
    fun part1(input: List<String>): Int {
        val blocks = parseBlocks(input)
        val allSand = mutableSetOf<Sand>()

        val lowestBlockY = blocks.maxOf { it.location.y }

        while (true) {
            val currSand = Sand(Coordinates(500, 0))
            val settledSand = allSand.toSet()
            allSand.add(currSand)

            while (!currSand.isSettled) {
                currSand.fall(blocks.map { it.location }.plus(settledSand.map { it.location }).toSet())

                if (currSand.location.y > lowestBlockY) return settledSand.size
            }
        }
    }

    fun part2(input: List<String>): Int {
        val blocks = parseBlocks(input)
        val allSand = mutableSetOf<Sand>()

        val lowestBlockY = blocks.maxOf { it.location.y }

        val infiniteFloorY = lowestBlockY + 2

        while (true) {
            val currSand = Sand(Coordinates(500, 0))
            val settledSand = allSand.toSet()
            allSand.add(currSand)

            while (!currSand.isSettled) {
                if (settledSand.contains(Sand(Coordinates(500, 0), true))) return settledSand.size

                currSand.fall(blocks.map { it.location }.plus(settledSand.map { it.location }).toSet(), infiniteFloorY)
            }
        }
    }

    val testInput = readInput("Day14_test")
    val input = readInput("Day14")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 24)
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test] : ${part2(testInput)}")
    check(part2(testInput) == 93)
    println("Part 2 [Real] : ${part2(input)}")
}
