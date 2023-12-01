fun main() {
    fun part1(input: List<String>): Int {
        var result: Int = 0

        for (line: String in input) {
            result += extractFirstAndLastDigitFromString(line)
        }

        return result
    }

    fun part2(input: List<String>): Int {
        var result: Int = 0

        for (line: String in input) {
            result += extractFirstAndLastDigitFromStringEnhanced(line)
        }

        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInputPartOne = readInput("Day01_test_one")
    check(part1(testInputPartOne) == 142)

    val testInputPartTwo = readInput("Day01_test_two")
    check(part2(testInputPartTwo) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

fun extractFirstAndLastDigitFromString(line: String): Int {
    var firstDigit: Char? = null
    var lastDigit: Char? = null

    for (char: Char in line.toCharArray()) {
        if (!char.isDigit()) {
            continue
        }

        if (firstDigit == null) {
            firstDigit = char
        }

        lastDigit = char
    }

    if (firstDigit == null) {
        return 0;
    }

    if (lastDigit == null) {
        return 0;
    }

    return (firstDigit.digitToInt()) * 10 + lastDigit.digitToInt()
}

fun extractFirstAndLastDigitFromStringEnhanced(line: String): Int {
    val numbers: Collection<String> = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    val firstWrittenNumber: Pair<Int, String>? = line.findAnyOf(numbers)
    var firstNumberIndex: Int? = firstWrittenNumber?.first
    var firstDigit: Int = 0

    if (firstWrittenNumber != null) {
        firstDigit = replaceWrittenWithDigit(firstWrittenNumber.second)
    }

    val lastWrittenNumber: Pair<Int, String>? = line.findLastAnyOf(numbers)
    var lastNumberIndex: Int? = lastWrittenNumber?.first
    var lastDigit: Int = 0

    if (lastWrittenNumber != null) {
        lastDigit = replaceWrittenWithDigit(lastWrittenNumber.second)
    }

    for (i in 0 until line.length) {
        val char: Char = line[i]

        if (!char.isDigit()) {
            continue;
        }

        if ((firstNumberIndex == null) || (i < firstNumberIndex)) {
            firstNumberIndex = i
            firstDigit = char.digitToInt()
        }

        if ((lastNumberIndex == null) || (i > lastNumberIndex)) {
            lastNumberIndex = i
            lastDigit = char.digitToInt()
        }
    }

    return firstDigit * 10 + lastDigit
}

fun replaceWrittenWithDigit(writtenDigit: String): Int {
    val digit: String = writtenDigit
        .replace("one", "1")
        .replace("two", "2")
        .replace("three", "3")
        .replace("four", "4")
        .replace("five", "5")
        .replace("six", "6")
        .replace("seven", "7")
        .replace("eight", "8")
        .replace("nine", "9")

    return digit.toInt()
}