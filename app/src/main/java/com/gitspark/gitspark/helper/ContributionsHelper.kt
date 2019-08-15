package com.gitspark.gitspark.helper

import com.gitspark.gitspark.model.Contribution
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val FILL_ATTR = "fill=\""
private const val DATA_COUNT_ATTR = "data-count=\""
private const val DATA_DATE_ATTR = "data-date=\""
private const val HEX_LENGTH = 7

@Singleton
class ContributionsHelper @Inject constructor() {

    fun parse(svg: String) {
        val contributions = arrayListOf<Contribution>()
        val scanner = Scanner(svg)

        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            if (line.containsOneOf(FILL_ATTR, DATA_COUNT_ATTR, DATA_DATE_ATTR)) {
                val fillStart = line.indexOf(FILL_ATTR) + FILL_ATTR.length
                val fillEnd = fillStart + HEX_LENGTH
                val fillColor = line.substring(fillStart, fillEnd)

                val countStart = line.indexOf(DATA_COUNT_ATTR) + DATA_COUNT_ATTR.length
                val countEnd = line.indexOf("\"", countStart)
                val numContributions = line.substring(countStart, countEnd)

                val dateStart = line.indexOf(DATA_DATE_ATTR) + DATA_DATE_ATTR.length
                val dateEnd = line.indexOf("\"", dateStart)
                val date = line.substring(dateStart, dateEnd)

                contributions.add(Contribution(
                    date = date,
                    numContributions = numContributions.toInt(),
                    fillColor = fillColor
                ))
            }
        }
        scanner.close()
    }

    fun getTotalContributions(svg: String): Int {
        val scanner = Scanner(svg)
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine().trim()
            if (line.contains("contributions") && Character.isDigit(line[0])) {
                return line.split(" ")[0].toInt()
            }
        }
        scanner.close()
        return -1
    }

    private fun String.containsOneOf(vararg strs: String): Boolean {
        return strs.any { this.contains(it) }
    }
}