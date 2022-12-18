package day18

import readInput
import java.util.*

data class Coordinate(
    val x: Int,
    val y: Int,
    val z: Int
) {
    val key = "$x $y $z"

    fun surrounding(): List<Coordinate> {
        return listOf(
            Coordinate(x = this.x + 1, y = this.y, z = this.z),
            Coordinate(x = this.x - 1, y = this.y, z = this.z),
            Coordinate(x = this.x, y = this.y + 1, z = this.z),
            Coordinate(x = this.x, y = this.y - 1, z = this.z),
            Coordinate(x = this.x, y = this.y, z = this.z + 1),
            Coordinate(x = this.x, y = this.y, z = this.z - 1)
        )
    }
}

class Graph {
    val nodes: MutableMap<String, Node> = mutableMapOf()
}

data class Node(val coordinate: Coordinate) {
    var shortestPath: List<Node> = LinkedList()
    var distance = Int.MAX_VALUE
    var adjacentNodes: MutableSet<Node> = mutableSetOf()

    fun addNeighbor(neighbor: Node) {
        this.adjacentNodes.add(neighbor)
        neighbor.adjacentNodes.add(this)
    }
}

fun calculateShortestPathFromSource(source: Node) {
    source.distance = 0
    val settledNodes: MutableSet<Node> = HashSet()
    val unsettledNodes: MutableSet<Node> = HashSet()
    unsettledNodes.add(source)
    while (unsettledNodes.size != 0) {
        val currentNode: Node = getLowestDistanceNode(unsettledNodes)
        unsettledNodes.remove(currentNode)
        currentNode.adjacentNodes.forEach { t ->
            if (!settledNodes.contains(t)) {
                calculateMinimumDistance(t, currentNode)
                unsettledNodes.add(t)
            }
        }
        settledNodes.add(currentNode)
    }
}

fun getLowestDistanceNode(unsettledNodes: Set<Node>): Node {
    var lowestDistanceNode: Node? = null
    var lowestDistance = Int.MAX_VALUE
    for (node in unsettledNodes) {
        val nodeDistance = node.distance
        if (nodeDistance < lowestDistance) {
            lowestDistance = nodeDistance
            lowestDistanceNode = node
        }
    }
    return lowestDistanceNode!!
}

fun calculateMinimumDistance(evaluationNode: Node, sourceNode: Node) {
    val sourceDistance = sourceNode.distance
    if (sourceDistance < evaluationNode.distance) {
        evaluationNode.distance = sourceDistance
        val shortestPath = LinkedList(sourceNode.shortestPath)
        shortestPath.add(sourceNode)
        evaluationNode.shortestPath = shortestPath
    }
}

fun parseCoordinates(input: List<String>): List<Coordinate> {
    return input.map { row ->
        row.split(",").let { tokens ->
            Coordinate(tokens[0].toInt(), tokens[1].toInt(), tokens[2].toInt())
        }
    }
}

fun createGraph(coordinates: Set<Coordinate>): Graph {
    return Graph().also { graph ->
        coordinates.forEach { coord ->
            Node(coord).also { node ->
                graph.nodes[coord.key] = node

                coord.surrounding().forEach { s ->
                    graph.nodes[s.key]?.also { neighbor ->
                        node.addNeighbor(neighbor)
                    }
                }
            }
        }
    }
}

fun totalSurfaceArea(coordinates: Set<Coordinate>): Int {
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

fun main() {
    fun part1(input: List<String>): Int {
        val coordinates = parseCoordinates(input)

        return totalSurfaceArea(coordinates.toSet())
    }

    fun part2(input: List<String>): Int {
        val coordinates = parseCoordinates(input).toSet()
        val coordKeys = coordinates.map { it.key }.toSet()

        val xMinOutside = coordinates.minOf { it.x } - 1
        val xMaxOutside = coordinates.maxOf { it.x } + 1

        val yMinOutside = coordinates.minOf { it.y } - 1
        val yMaxOutside = coordinates.maxOf { it.y } + 1

        val zMinOutside = coordinates.minOf { it.z } - 1
        val zMaxOutside = coordinates.maxOf { it.z } + 1

        val emptyCoordinates = mutableSetOf<Coordinate>()

        for (x in xMinOutside..xMaxOutside) {
            for (y in yMinOutside..yMaxOutside) {
                for (z in zMinOutside..zMaxOutside) {
                    Coordinate(x, y, z).also { coord ->
                        if (!coordKeys.contains(coord.key)) {
                            emptyCoordinates.add(coord)
                        }
                    }
                }
            }
        }

        val emptyGraph = createGraph(emptyCoordinates)
        val outerNode = emptyGraph.nodes["$xMinOutside $yMinOutside $zMinOutside"]!!

        calculateShortestPathFromSource(outerNode)

        val enclosedEmptyNodes = emptyGraph.nodes.values.filter { it.distance == Int.MAX_VALUE }

        var surfaceArea = totalSurfaceArea(coordinates)

        enclosedEmptyNodes.forEach { node ->
            node.coordinate.surrounding().forEach { s ->
                if (coordKeys.contains(s.key)) surfaceArea -= 1
            }
        }

        return surfaceArea
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
