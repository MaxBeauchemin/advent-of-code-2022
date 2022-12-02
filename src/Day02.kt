enum class RPSChoice(val defaultPoints: Int) {
    ROCK(1),
    PAPER(2),
    SCISSORS(3);

    fun beats(other: RPSChoice): Boolean {
        return when (this) {
            ROCK -> other == SCISSORS
            PAPER -> other == ROCK
            SCISSORS -> other == PAPER
        }
    }

    companion object {
        fun fromString(input: String): RPSChoice {
            return when (input) {
                "A", "X" -> ROCK
                "B", "Y" -> PAPER
                "C", "Z" -> SCISSORS
                else -> throw Exception("Can't Parse Input into RPSChoice")
            }
        }

        fun desiredChoice(opponentChoice: RPSChoice, desiredGameResult: GameResult): RPSChoice {
            return when (desiredGameResult) {
                GameResult.WIN -> RPSChoice.values().find { it.beats(opponentChoice) }!!
                GameResult.DRAW -> opponentChoice
                GameResult.LOSS -> RPSChoice.values().find { opponentChoice.beats(it) }!!
            }
        }
    }
}

enum class GameResult(val points: Int) {
    WIN(6),
    DRAW(3),
    LOSS(0);

    companion object {
        fun fromString(input: String): GameResult {
            return when (input) {
                "X" -> LOSS
                "Y" -> DRAW
                "Z" -> WIN
                else -> throw Exception("Can't Parse Input into GameResult")
            }
        }
    }
}

fun main() {
    fun calcRoundPoints(opponentChoice: RPSChoice, yourChoice: RPSChoice): Int {
        val roundResult = if (opponentChoice == yourChoice) {
            // Draw
            GameResult.DRAW
        } else if (yourChoice.beats(opponentChoice)) {
            // Win
            GameResult.WIN
        } else {
            // Loss
            GameResult.LOSS
        }

        return yourChoice.defaultPoints + roundResult.points
    }

    // Output -> Opponent Choice / Your Recommended Choice
    fun parseRoundPart1(input: String): Pair<RPSChoice, RPSChoice> {
        return input.split(" ").let { tokens ->
            if (tokens.size != 2) throw Exception("Invalid Round Input")

            RPSChoice.fromString(tokens[0]) to RPSChoice.fromString(tokens[1])
        }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { round ->
            parseRoundPart1(round).let { (opponentChoice, yourRecommendedChoice) ->
                calcRoundPoints(opponentChoice, yourRecommendedChoice)
            }
        }
    }

    // Output -> Opponent Choice / Desired Game Result
    fun parseRoundPart2(input: String): Pair<RPSChoice, GameResult> {
        return input.split(" ").let { tokens ->
            if (tokens.size != 2) throw Exception("Invalid Round Input")

            RPSChoice.fromString(tokens[0]) to GameResult.fromString(tokens[1])
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { round ->
            parseRoundPart2(round).let { (opponentChoice, desiredGameResult) ->
                RPSChoice.desiredChoice(opponentChoice, desiredGameResult).let { yourChoice ->
                    calcRoundPoints(opponentChoice, yourChoice)
                }
            }
        }
    }

    val testInput = readInput("Day02_test")
    println(part1(testInput))
    check(part1(testInput) == 15)
    println(part2(testInput))
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
