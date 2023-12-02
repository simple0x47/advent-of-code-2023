import java.util.*
import kotlin.collections.ArrayList

enum class CubeColors {
    RED,
    BLUE,
    GREEN
}

class GameSet() {
    val cubes: EnumMap<CubeColors, Int> = EnumMap(CubeColors::class.java)
}

class Game(val id: Int, val sets: Collection<GameSet>) {
    private fun getColorMax(color: CubeColors): Int {
        var max = 0

        for (set in sets) {
            val cubes = set.cubes[color] ?: continue

            if (cubes > max) {
                max = cubes
            }
        }

        return max
    }

    fun isPossible(set: GameSet): Boolean {
        for (color: CubeColors in CubeColors.entries) {
            val cubes = getColorMax(color)
            val actualCubes = set.cubes[color] ?: 0

            if (cubes > actualCubes) {
                return false
            }
        }

        return true
    }

    fun getMaximumSet(): GameSet {
        val set = GameSet()

        for (color in CubeColors.entries) {
            set.cubes[color] = getColorMax(color)
        }

        return set
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val inputSet = GameSet()
        inputSet.cubes[CubeColors.RED] = 12
        inputSet.cubes[CubeColors.GREEN] = 13
        inputSet.cubes[CubeColors.BLUE] = 14

        var sum = 0

        for (line: String in input) {
            val game = extractGameFromLine(line)

            if (!game.isPossible(inputSet)) {
                continue;
            }

            sum += game.id
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        var sum = 0

        for (line: String in input) {
            val game = extractGameFromLine(line)

            val maximumSet = game.getMaximumSet()

            var power = 1

            for (color in CubeColors.entries) {
                power *= maximumSet.cubes[color] ?: 0
            }

            sum += power
        }

        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInputPartOne = readInput("Day02_test_one")
    check(part1(testInputPartOne) == 8)

    val testInputPartTwo = readInput("Day02_test_one")
    check(part2(testInputPartTwo) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

fun extractGameFromLine(line: String): Game {
    val id: Int = extractGameIdFromLine(line) ?: throw IllegalArgumentException("failed to read 'id' from line: $line")

    val setsStartIndex = line.indexOf(':')
    val setsStringList = line.substring(setsStartIndex).split(";")
    val sets = extractGameSetsFromStringList(setsStringList)

    return Game(id, sets)
}

fun extractGameIdFromLine(line: String): Int? {
    val firstNumberRegex = Regex("\\d+")

    val matchResult = firstNumberRegex.find(line)

    return matchResult?.value?.toInt()
}

fun extractGameSetsFromStringList(setsStringList: List<String>): List<GameSet> {
    val regex = Regex("(\\d+)\\s+(\\w+)")
    val sets = ArrayList<GameSet>()

    for (setString: String in setsStringList) {
        val set: GameSet = GameSet()
        val matches = regex.findAll(setString)

        for (match: MatchResult in matches) {
            val (amount, color) = match.destructured

            val (cubes, cubesColor) = Pair(amount.toInt(), getColorFromString(color))

            set.cubes[cubesColor] = cubes
        }

        sets.add(set)
    }

    return sets
}

fun getColorFromString(color: String): CubeColors {
    if (color == "red") {
        return CubeColors.RED
    } else if (color == "blue") {
        return CubeColors.BLUE
    } else if (color == "green") {
        return CubeColors.GREEN
    } else {
        return CubeColors.RED
    }
}