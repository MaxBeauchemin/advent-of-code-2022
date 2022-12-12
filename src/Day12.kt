import java.lang.Math.abs
import java.util.*

class Graph {
    val nodes: MutableSet<Node> = HashSet()
    fun findByKey(key: String) = nodes.find { it.key == key }!!
}

class Node(
        val key: String,
        val elevation: Int,
        val isStart: Boolean,
        val isEnd: Boolean
) {
    var shortestPath: List<Node> = LinkedList()
    var distance = Int.MAX_VALUE
    var adjacentNodes: MutableMap<Node, Int> = HashMap()

    // Reversed Direction (End to Start)
    fun addNeighborConditionally(neighbor: Node) {
        if (abs(this.elevation - neighbor.elevation) <= 1) {
            this.adjacentNodes.put(neighbor, 1)
            neighbor.adjacentNodes.put(this, 1)
        } else if (neighbor.elevation < this.elevation) {
            neighbor.adjacentNodes.put(this, 1)
        } else {
            this.adjacentNodes.put(neighbor, 1)
        }
    }
}

fun calculateShortestPathFromSource(graph: Graph, source: Node) {
    source.distance = 0
    val settledNodes: MutableSet<Node> = HashSet()
    val unsettledNodes: MutableSet<Node> = HashSet()
    unsettledNodes.add(source)
    while (unsettledNodes.size != 0) {
        val currentNode: Node = getLowestDistanceNode(unsettledNodes)
        unsettledNodes.remove(currentNode)
        currentNode.adjacentNodes.forEach { (t, u) ->
            if (!settledNodes.contains(t)) {
                calculateMinimumDistance(t, u, currentNode)
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

fun calculateMinimumDistance(evaluationNode: Node, edgeWeigh: Int, sourceNode: Node) {
    val sourceDistance = sourceNode.distance
    if (sourceDistance + edgeWeigh < evaluationNode.distance) {
        evaluationNode.distance = sourceDistance + edgeWeigh
        val shortestPath = LinkedList(sourceNode.shortestPath)
        shortestPath.add(sourceNode)
        evaluationNode.shortestPath = shortestPath
    }
}

fun main() {
    fun parse(input: List<String>): Graph {
        val graph = Graph()

        // Top -> Down
        // Left -> Right
        input.forEachIndexed { o, row ->
            row.forEachIndexed { i, char ->
                val isStart = char == 'S'
                val isEnd = char == 'E'
                val elevation = if (isStart) 0 else if (isEnd) 25 else char.code - 97

                val currNode = Node("$o $i", elevation, isStart, isEnd)

                graph.nodes.add(currNode)

                // Check Neighbor Up
                if (o > 0) {
                    graph.findByKey("${o - 1} $i").also { neighbor ->
                        currNode.addNeighborConditionally(neighbor)
                    }
                }

                // Check Neighbor Left
                if (i > 0) {
                    graph.findByKey("$o ${i - 1}").also { neighbor ->
                        currNode.addNeighborConditionally(neighbor)
                    }
                }
            }
        }

        return graph
    }

    fun part1(input: List<String>): Int {
        val graph = parse(input)

        calculateShortestPathFromSource(graph, graph.nodes.find { it.isEnd }!!)

        return graph.nodes.find { it.isStart }!!.distance
    }

    fun part2(input: List<String>): Int {
        val graph = parse(input)

        calculateShortestPathFromSource(graph, graph.nodes.find { it.isEnd }!!)

        return graph.nodes.filter { it.elevation == 0 }.minOf { it.distance }
    }

    val testInput = readInput("Day12_test")
    val input = readInput("Day12")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 31)
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test] : ${part2(testInput)}")
    check(part2(testInput) == 29)
    println("Part 2 [Real] : ${part2(input)}")
}
