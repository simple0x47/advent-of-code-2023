enum class EngineSchematicItemType {
    NUMBER,
    SYMBOL,
    DOT
}

class EngineSchematicTransform(val x: Int, val y: Int, val sizeX: Int, val sizeY: Int)

open class EngineSchematicItem(val type: EngineSchematicItemType, val value: String, val transform: EngineSchematicTransform) {

}

class EngineSchematicPossibleGear(transform: EngineSchematicTransform) :
    EngineSchematicItem(EngineSchematicItemType.SYMBOL, "*", transform) {
    private val partNumbers = ArrayList<Int>()

    fun addPartNumber(number: Int) {
        partNumbers.add(number)
    }

    fun getGearRatio(): Int {
        if (!isGear()) {
            return 0;
        }

        var ratio = 1

        for (partNumber in partNumbers) {
            ratio *= partNumber
        }

        return ratio
    }

    private fun isGear(): Boolean {
        return partNumbers.size > 1
    }
}

class EngineSchematic(val schematic: Array<Array<EngineSchematicItem?>>) {
    private val numberRegex: Regex = Regex("(\\d+)")
    private val symbolRegex: Regex = Regex("[^\\w\\s]")
    private var nextRowY: Int = 0

    private val numberList = ArrayList<EngineSchematicItem>()
    private val possibleGearList = ArrayList<EngineSchematicPossibleGear>()

    fun addRow(line: String) {
        val numberMatches = numberRegex.findAll(line)

        for (numberMatch in numberMatches) {
            val transform = EngineSchematicTransform(numberMatch.range.first, nextRowY, numberMatch.range.last - numberMatch.range.first + 1, 1)

            val number: EngineSchematicItem = EngineSchematicItem(EngineSchematicItemType.NUMBER, numberMatch.value, transform)
            schematic[transform.x][transform.y] = number
            numberList.add(number)
        }

        val symbolMatches = symbolRegex.findAll(line)

        for (symbolMatch in symbolMatches) {
            val transform = EngineSchematicTransform(symbolMatch.range.first, nextRowY, 1, 1)

            if (symbolMatch.value == ".") {
                schematic[transform.x][transform.y] = EngineSchematicItem(EngineSchematicItemType.DOT, symbolMatch.value, transform)
            } else if (symbolMatch.value == "*") {
                val gear = EngineSchematicPossibleGear(transform)
                schematic[transform.x][transform.y] = gear
                possibleGearList.add(gear)
            } else {
                schematic[transform.x][transform.y] = EngineSchematicItem(EngineSchematicItemType.SYMBOL, symbolMatch.value, transform)
            }
        }

        nextRowY++
    }

    fun sumNumbersAdjacentToSymbol(): Int {
        var sum = 0

        for (number in numberList) {
            if (isNumberAdjacentToSymbol(number)) {
                sum += number.value.toInt()
            }
        }

        return sum
    }

    fun sumGearRatios(): Int {
        var sum = 0

        for (possibleGear in possibleGearList) {
            sum += possibleGear.getGearRatio()
        }

        return sum
    }

    private fun isNumberAdjacentToSymbol(number: EngineSchematicItem): Boolean {
        if (number.transform.y > 0) {
            if (isNumberAdjacentVerticallyToSymbol(number, number.transform.y - 1)) {
                return true
            }
        }

        if (number.transform.x > 0) {
            val leftItem = schematic[number.transform.x - 1][number.transform.y]

            if (leftItem != null && leftItem.type == EngineSchematicItemType.SYMBOL) {
                addPartNumberIfItemIsAPossibleGear(leftItem, number)

                return true
            }
        }

        if (number.transform.x + number.transform.sizeX < schematic.size) {
            val rightItem = schematic[number.transform.x + number.transform.sizeX][number.transform.y]

            if (rightItem != null && rightItem.type == EngineSchematicItemType.SYMBOL) {
                addPartNumberIfItemIsAPossibleGear(rightItem, number)

                return true
            }
        }

        if (number.transform.y < nextRowY - 1) {
            if (isNumberAdjacentVerticallyToSymbol(number, number.transform.y + 1)) {
                return true
            }
        }

        return false
    }

    private fun isNumberAdjacentVerticallyToSymbol(number: EngineSchematicItem, y: Int): Boolean {
        if (number.transform.x > 0) {
            val verticalLeftItem = schematic[number.transform.x - 1][y]

            if (verticalLeftItem != null && verticalLeftItem.type == EngineSchematicItemType.SYMBOL) {
                addPartNumberIfItemIsAPossibleGear(verticalLeftItem, number)

                return true
            }
        }

        for (x in number.transform.x until number.transform.x + number.transform.sizeX) {
            val verticalItem = schematic[x][y]

            if (verticalItem != null && verticalItem.type == EngineSchematicItemType.SYMBOL) {
                addPartNumberIfItemIsAPossibleGear(verticalItem, number)

                return true
            }
        }

        if (number.transform.x + number.transform.sizeX < schematic.size) {
            val verticalRightItem = schematic[number.transform.x + number.transform.sizeX][y]

            if (verticalRightItem != null && verticalRightItem.type == EngineSchematicItemType.SYMBOL) {
                addPartNumberIfItemIsAPossibleGear(verticalRightItem, number)

                return true
            }
        }

        return false
    }
}

private fun addPartNumberIfItemIsAPossibleGear(item: EngineSchematicItem, number: EngineSchematicItem) {
    if (item is EngineSchematicPossibleGear) {
        item.addPartNumber(number.value.toInt())
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val sizeX = input.first().length
        val sizeY = input.size
        val schematicMap: Array<Array<EngineSchematicItem?>> = Array(sizeX, { i -> Array(sizeY, { it -> null })})

        val schematic = EngineSchematic(schematicMap)

        for (line in input) {
            schematic.addRow(line)
        }

        return schematic.sumNumbersAdjacentToSymbol()
    }

    fun part2(input: List<String>): Int {
        val sizeX = input.first().length
        val sizeY = input.size
        val schematicMap: Array<Array<EngineSchematicItem?>> = Array(sizeX, { i -> Array(sizeY, { it -> null })})

        val schematic = EngineSchematic(schematicMap)

        for (line in input) {
            schematic.addRow(line)
        }

        schematic.sumNumbersAdjacentToSymbol()

        return schematic.sumGearRatios()
    }

    // test if implementation meets criteria from the description, like:
    val testInputPartOne = readInput("Day03_test_one")
    check(part1(testInputPartOne) == 4361)

    val testInputPartTwo = readInput("Day03_test_one")
    check(part2(testInputPartTwo) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}