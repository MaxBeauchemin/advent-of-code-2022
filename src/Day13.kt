interface PacketContent {
    val type: String
}

data class PacketItem(
    val value: Int
) : PacketContent {
    override val type: String = "Item"
}

data class PacketArray(
    val items: List<PacketContent>
) : PacketContent {
    override val type: String = "Array"
}

fun isOrdered(left: PacketContent, right: PacketContent): Boolean? {
    val sameTypes = left.type == right.type

    return if (sameTypes) {
        when (left.type) {
            "Item" -> {
                (left as PacketItem to right as PacketItem).let { (l, r) ->
                    if (l.value == r.value) null else l.value < r.value
                }
            }
            "Array" -> {
                (left as PacketArray to right as PacketArray).let { (l, r) ->
                    arraysOrdered(l, r)
                }
            }
            else -> throw Exception("Unknown Type")
        }
    } else {
        if (left.type == "Array") {
            (left as PacketArray to right as PacketItem).let { (l, r) ->
                arraysOrdered(l, PacketArray(listOf(r)))
            }
        } else {
            (left as PacketItem to right as PacketArray).let { (l, r) ->
                arraysOrdered(PacketArray(listOf(l)), r)
            }
        }
    }
}

fun arraysOrdered(left: PacketArray, right: PacketArray): Boolean? {
    return left.items.zip(right.items).let { pairs ->
        pairs.firstNotNullOfOrNull { isOrdered(it.first, it.second) }
    } ?: if (left.items.size == right.items.size) null else left.items.size < right.items.size
}

fun parseContent(input: String): PacketContent {
    return if (input.first() == '[' && input.last() == ']') {
        parseArray(input)
    } else {
        parseItem(input)
    }
}

fun parseArray(input: String): PacketArray {
    // Empty
    if (input == "[]") return PacketArray(emptyList())

    // Splitting by "," isn't reliable enough, we must exclude "," inside brackets
    val arrayItems = mutableListOf<String>()
    input.removePrefix("[").removeSuffix("]").let { innerString ->
        var bracketDepth = 0
        var currBuffer = ""
        innerString.toList().forEach { char ->
            var skipBuffer = false
            when (char) {
                '[' -> bracketDepth++
                ']' -> bracketDepth--
                ',' -> if (bracketDepth == 0) {
                    skipBuffer = true
                    arrayItems.add(currBuffer)
                    currBuffer = ""
                }
            }
            if (!skipBuffer) currBuffer += char
        }
        arrayItems.add(currBuffer)
    }

    return arrayItems.map { str ->
        parseContent(str)
    }.let { contents ->
        PacketArray(contents)
    }
}

fun parseItem(input: String): PacketItem {
    return PacketItem(input.toInt())
}

fun main() {
    fun part1(input: List<String>): Int {
        var orderedSum = 0
        input.chunked(3).forEachIndexed { index, packetGroup ->
            val packetOne = parseContent(packetGroup[0])
            val packetTwo = parseContent(packetGroup[1])

            if (isOrdered(packetOne, packetTwo) == true) {
                orderedSum += (index + 1)
            }
        }
        return orderedSum
    }

    fun part2(input: List<String>): Int {
        val dividerTwoPacket = parseContent("[[2]]")
        val dividerSixPacket = parseContent("[[6]]")

        val packets = input.mapNotNull { packet ->
            if (packet.isEmpty()) null
            else parseContent(packet)
        }.plus(dividerTwoPacket).plus(dividerSixPacket)

        val sortedPackets = packets.sortedWith(fun (a: PacketContent, b: PacketContent): Int {
            return if (isOrdered(a, b) == true) -1 else 1
        })

        val idxOfTwo = sortedPackets.indexOf(dividerTwoPacket)
        val idxOfSix = sortedPackets.indexOf(dividerSixPacket)

        return (idxOfTwo + 1) * (idxOfSix + 1)
    }

    val testInput = readInput("Day13_test")
    val input = readInput("Day13")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 13)
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test] : ${part2(testInput)}")
    check(part2(testInput) == 140)
    println("Part 2 [Real] : ${part2(input)}")
}
