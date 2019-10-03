package com.gitspark.gitspark.helper

import android.annotation.SuppressLint
import com.gitspark.gitspark.extension.containsAll
import com.gitspark.gitspark.extension.monthValue
import com.gitspark.gitspark.model.Contribution
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

private const val FILL_ATTR = "fill=\""
private const val DATA_COUNT_ATTR = "data-count=\""
private const val DATA_DATE_ATTR = "data-date=\""
private const val HEX_LENGTH = 7

@Singleton
class ContributionsHelper @Inject constructor() {

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val calendar = Calendar.getInstance()

    fun parse(svg: String): SortedMap<String, List<Contribution>> {
        if (svg.isEmpty()) return sortedMapOf()

        val contributions = arrayListOf<Contribution>()
        val scanner = Scanner(svg)

        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            if (line.containsAll(FILL_ATTR, DATA_COUNT_ATTR, DATA_DATE_ATTR)) {
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

        val contributionsMap = mutableMapOf<String, ArrayList<Contribution>>()
        contributions.forEach { contribution ->
            val date = sdf.parse(contribution.date)
            calendar.time = date
            val key = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " +
                    calendar.get(Calendar.YEAR)

            if (!contributionsMap.containsKey(key)) {
                contributionsMap[key] = arrayListOf()
            }
            contributionsMap[key]?.add(contribution)
        }

        return contributionsMap.toSortedMap(kotlin.Comparator { o1, o2 ->
            val key1 = o1.split(" ")
            val key2 = o2.split(" ")

            if (key1[1] == key2[1])
                key2[0].monthValue() - key1[0].monthValue()
            else
                key2[1].toInt() - key1[1].toInt()
        })
    }

    fun getTotalContributions(svg: String): Int {
        val scanner = Scanner(svg)
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine().trim()
            if (line.contains("contribution") && Character.isDigit(line[0])) {
                return line.split(" ")[0].replace(",", "").toInt()
            }
        }
        scanner.close()
        return -1
    }
}