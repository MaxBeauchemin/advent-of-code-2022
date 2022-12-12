data class Spot(
    val elevation: Int,
    val isStart: Boolean,
    val isEnd: Boolean
)

fun Pair<Int, Int>.key() = "${first}_$second"

fun List<List<Spot>>.get(coords: Pair<Int, Int>): Spot {
    return this[coords.first][coords.second]!!
}

fun unvisitedNeighbors(coords: Pair<Int, Int>, grid: List<List<Spot>>, visited: Set<String>): List<Pair<Int, Int>> {
    val neighbors = mutableListOf<Pair<Int, Int>>()

    if (coords.first != 0) {
        neighbors.add(coords.first - 1 to coords.second)
    }

    if (coords.second != 0) {
        neighbors.add(coords.first to coords.second - 1)
    }

    if (coords.first < (grid.size - 1)) {
        neighbors.add(coords.first + 1 to coords.second)
    }

    if (coords.second < (grid[0].size - 1)) {
        neighbors.add(coords.first to coords.second + 1)
    }

    return neighbors.filter { !visited.contains(it.key()) }
}

fun main() {
    fun parse(input: List<String>): List<List<Spot>> {
        return input.map { row ->
            row.map { cell ->
                val isStart = cell == 'S'
                val isEnd = cell == 'E'
                val elevation = if (isStart) 0
                else if (isEnd) 25
                else cell.code - 97

                Spot(
                    elevation = elevation,
                    isStart = isStart,
                    isEnd = isEnd
                )
            }
        }
    }

    fun part1(input: List<String>): Int {
        val grid = parse(input)

        val spotDistances = mutableMapOf<String, Int>()

        fun findMinPathToEnd(currCoords: Pair<Int, Int>, currStepCount: Int, visited: Set<String>): Int {
            val currSpot = grid.get(currCoords)

            if (currSpot.isEnd) {
                spotDistances[currCoords.key()] = 0
                return 0
            }
//            spotDistances[currCoords.key()].let {
//                if (it != null) return it
//            }

            return unvisitedNeighbors(currCoords, grid, visited).filter { neighborCoords ->
                grid.get(neighborCoords).let { neighbor ->
                    currSpot.elevation <= neighbor.elevation + 1
                }
            }.let { options ->
                if (options.none()) return 100000000
                else {
                    options.minOf { o ->
                        findMinPathToEnd(o, currStepCount + 1, visited.plus(currCoords.key()))
                    }.also {
                        spotDistances[currCoords.key()] = currStepCount + 1 + it
                    }
                }
            }
        }

        var endOuterIdx = -1
        var endInnerIdx = -1
        var startOuterIdx = -1
        var startInnerIdx = -1

        grid.forEachIndexed { o, spots ->
            spots.forEachIndexed { i, spot ->
                if (spot.isEnd) {
                    endOuterIdx = o
                    endInnerIdx = i
                }
                if (spot.isStart) {
                    startOuterIdx = o
                    startInnerIdx = i
                }
            }
        }

        // fill spotDistances map
        var toCheck = mutableSetOf<Pair<Int, Int>>()
        toCheck.add(endOuterIdx to endInnerIdx)
        while (toCheck.any()) {
            val toCheckNext = mutableListOf<Pair<Int, Int>>()
            toCheck.forEach { c ->
                findMinPathToEnd(c, 0, emptySet())
                toCheckNext.addAll(unvisitedNeighbors(c, grid, spotDistances.keys))
            }
            toCheck.clear()
            toCheck.addAll(toCheckNext)
        }

        return spotDistances["${startOuterIdx}_$startInnerIdx"]!!
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day12_test")
    val input = readInput("Day12")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 31)
    println("Part 1 [Real] : ${part1(input)}")

//    println("Part 2 [Test] : ${part2(testInput)}")
//    check(part2(testInput) == 2713310)
//    println("Part 2 [Real] : ${part2(input)}")
}
