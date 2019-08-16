package com.gitspark.gitspark.helper

import com.gitspark.gitspark.model.Contribution
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

private const val SAMPLE_SVG = "<rect class=\"day\" width=\"12\" height=\"12\" x=\"16\" y=\"0\" fill=\"#7bc96f\" data-count=\"5\" data-date=\"2018-08-12\"/>\n" +
        "          <rect class=\"day\" width=\"12\" height=\"12\" x=\"16\" y=\"15\" fill=\"#c6e48b\" data-count=\"4\" data-date=\"2018-08-13\"/>\n" +
        "          <rect class=\"day\" width=\"12\" height=\"12\" x=\"16\" y=\"30\" fill=\"#c6e48b\" data-count=\"3\" data-date=\"2018-08-14\"/>\n"
private const val SAMPLE_TOTAL_CONTRIBUTIONS = "<h2 class=\"f4 text-normal mb-2\">\n" +
        "      877 contributions\n" +
        "        in the last year\n" +
        "    </h2>"

class ContributionsHelperTest {

    private lateinit var contributionsHelper: ContributionsHelper

    @Before
    fun setup() {
        contributionsHelper = ContributionsHelper()
    }

    @Test
    fun shouldParseSvg() {
        val map = contributionsHelper.parse(SAMPLE_SVG)

        assertThat(map).containsKey("August 2018")
        assertThat(map["August 2018"]).isEqualTo(
            listOf(
                Contribution(
                    numContributions = 5,
                    date = "2018-08-12",
                    fillColor = "#7bc96f"
                ),
                Contribution(
                    numContributions = 4,
                    date = "2018-08-13",
                    fillColor = "#c6e48b"
                ),
                Contribution(
                    numContributions = 3,
                    date = "2018-08-14",
                    fillColor = "#c6e48b"
                )
            )
        )
    }

    @Test
    fun shouldParseEmptySvg() {
        val map = contributionsHelper.parse("")
        assertThat(map).isEmpty()
    }

    @Test
    fun shouldGetTotalContributions() {
        val num = contributionsHelper.getTotalContributions(SAMPLE_TOTAL_CONTRIBUTIONS)
        assertThat(num).isEqualTo(877)
    }

    @Test
    fun shouldGetTotalContributionsWhenNotExisting() {
        val num = contributionsHelper.getTotalContributions("")
        assertThat(num).isEqualTo(-1)
    }
}