package day07

import readInput
import java.util.*

data class File(val name: String, val bytes: Int)

data class Directory(val name: String, val files: MutableList<File> = mutableListOf(), val directories: MutableList<Directory> = mutableListOf(), val uuid: UUID = UUID.randomUUID()) {
    fun totalBytes(): Int {
        return files.sumOf { it.bytes } + directories.sumOf { it.totalBytes() }
    }
    fun findParentDirectoryOf(uuid: UUID): Directory? {
        return if (this.directories.any { it.uuid == uuid }) this
        else directories.firstNotNullOfOrNull {
            it.findParentDirectoryOf(uuid)
        }
    }
}

enum class CommandType {
    CD_ROOT, CD_UP, CD_IN, LS
}

data class Command(val type: CommandType, val param: String = "", val output: List<String> = emptyList())

fun main() {
    fun parseDirectoryContents(input: List<String>): Pair<List<Directory>, List<File>> {
        val directories = mutableListOf<Directory>()
        val files = mutableListOf<File>()

        input.forEach { i ->
            if (i.startsWith("dir ")) {
                directories.add(Directory(i.replace("dir ", "")))
            } else {
                i.split(" ").also { tokens ->
                    files.add(File(tokens[1], tokens[0].toInt()))
                }
            }
        }

        return directories to files
    }

    fun parseCommand(input: String, output: List<String>): Command {
        return if (input == "$ ls") Command(CommandType.LS, output = output)
        else if (input.startsWith("$ cd ")) {
            if (input.endsWith("/")) Command(CommandType.CD_ROOT)
            else if (input.endsWith("..")) Command(CommandType.CD_UP)
            else Command(CommandType.CD_IN, input.replace("$ cd ", ""))
        } else throw Exception("Invalid Command")
    }

    fun parseDirectories(input: List<String>): Directory {
        val rootDirectory = Directory("/")
        var currDirectory = rootDirectory

        fun execCommand(cmd: Command) {
            runCatching {
                when (cmd.type) {
                    CommandType.CD_ROOT -> currDirectory = rootDirectory
                    CommandType.CD_UP -> currDirectory = rootDirectory.findParentDirectoryOf(currDirectory.uuid)!!
                    CommandType.CD_IN -> currDirectory = currDirectory.directories.find { it.name == cmd.param }!!
                    CommandType.LS -> {
                        parseDirectoryContents(cmd.output).also { parsed ->
                            currDirectory.directories.addAll(parsed.first)
                            currDirectory.files.addAll(parsed.second)
                        }
                    }
                }
            }
        }

        var nextCommand = ""
        val commandBuffer = mutableListOf<String>()

        input.forEach { i ->
            if (i.startsWith("$")) {
                if (nextCommand != "") {
                    // We can now parse the previous command, since we know its output
                    parseCommand(nextCommand, commandBuffer).also {
                        execCommand(it)
                    }
                }

                nextCommand = i
                commandBuffer.clear()
            } else {
                commandBuffer.add(i)
            }
        }

        // Need to manually do this for last one
        parseCommand(nextCommand, commandBuffer).also {
            execCommand(it)
        }

        return rootDirectory
    }

    // Find all of the directories with a total size of at most 100000.
    // What is the sum of the total sizes of those directories?
    fun part1(input: List<String>): Int {
        fun sumOfSizesUnderOrEqual(currDirectory: Directory, maxSize: Int): Int {
            val bytes = currDirectory.totalBytes().takeIf { it <= maxSize } ?: 0

            return bytes + currDirectory.directories.sumOf {
                sumOfSizesUnderOrEqual(it, maxSize)
            }
        }

        return sumOfSizesUnderOrEqual(parseDirectories(input), 100000)
    }

    // Total System Space: 70,000,000
    // Space Needed: 30,000,000
    // Find the smallest directory that, if deleted, would free up enough space on the filesystem to run the update.
    // What is the total size of that directory?
    fun part2(input: List<String>): Int {
        val totalSpace = 70000000
        val spaceNeeded = 30000000

        val rootDirectory = parseDirectories(input)

        val totalSpaceOccupied = rootDirectory.totalBytes()
        val minBytesToDelete = spaceNeeded - (totalSpace - totalSpaceOccupied)

        fun flattenedDirectorySizes(currDirectory: Directory): Map<UUID, Int> {
            return mutableMapOf(currDirectory.uuid to currDirectory.totalBytes()).also { map ->
                currDirectory.directories.forEach { dir ->
                    map.putAll(flattenedDirectorySizes(dir))
                }
            }
        }

        val toDelete = flattenedDirectorySizes(rootDirectory).let { map ->
            map.toList().sortedBy { it.second }.first { it.second >= minBytesToDelete }
        }

        return toDelete.second
    }

    val testInput = readInput("Day07_test")
    val input = readInput("Day07")

    println("Part 1 [Test] : ${part1(testInput)}")
    check(part1(testInput) == 95437)
    println("Part 1 [Real] : ${part1(input)}")

    println("Part 2 [Test] : ${part2(testInput)}")
    check(part2(testInput) == 24933642)
    println("Part 2 [Real] : ${part2(input)}")
}
