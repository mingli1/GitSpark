package com.gitspark.gitspark.helper

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

private const val SAMPLE_RESPONSE = "<div class=\"Box pinned-item-list-item d-flex p-3 width-full js-pinned-item-list-item public source reorderable sortable-button-item\">\n" +
        "    <div class=\"pinned-item-list-item-content\">\n" +
        "      <div class=\"d-flex width-full flex-items-center position-relative\">\n" +
        "        <svg class=\"octicon octicon-repo mr-2 text-gray flex-shrink-0\" viewBox=\"0 0 12 16\" version=\"1.1\" width=\"12\" height=\"16\" aria-hidden=\"true\"><path fill-rule=\"evenodd\" d=\"M4 9H3V8h1v1zm0-3H3v1h1V6zm0-2H3v1h1V4zm0-2H3v1h1V2zm8-1v12c0 .55-.45 1-1 1H6v2l-1.5-1.5L3 16v-2H1c-.55 0-1-.45-1-1V1c0-.55.45-1 1-1h10c.55 0 1 .45 1 1zm-1 10H1v2h2v-1h3v1h5v-2zm0-10H2v9h9V1z\"></path></svg>\n" +
        "        <a href=\"/mingli1/Unlucky\" class=\"text-bold flex-auto min-width-0 \">\n" +
        "          <span class=\"repo js-pinnable-item\" title=\"Unlucky\">Unlucky</span>\n" +
        "        </a>\n" +
        "        \n" +
        "          <input type=\"hidden\" name=\"pinned_item_ids[]\" id=\"pinned-item-reorder-MDEwOlJlcG9zaXRvcnkxMDkyMDUzNDM=\" value=\"MDEwOlJlcG9zaXRvcnkxMDkyMDUzNDM=\" class=\"form-control\">\n" +
        "          <span class=\"pinned-item-handle js-pinned-item-reorder pl-2\" title=\"Drag to reorder\">\n" +
        "            <svg class=\"octicon octicon-grabber\" viewBox=\"0 0 8 16\" version=\"1.1\" width=\"8\" height=\"16\" aria-hidden=\"true\"><path fill-rule=\"evenodd\" d=\"M8 4v1H0V4h8zM0 8h8V7H0v1zm0 3h8v-1H0v1z\"></path></svg>\n" +
        "          </span>\n" +
        "          <button type=\"button\" class=\"btn btn-outline btn-sm show-on-focus sortable-button js-sortable-button right-0\" data-direction=\"up\"><svg aria-label=\"Move Unlucky up\" class=\"octicon octicon-chevron-up\" viewBox=\"0 0 10 16\" version=\"1.1\" width=\"10\" height=\"16\" role=\"img\"><path fill-rule=\"evenodd\" d=\"M10 10l-1.5 1.5L5 7.75 1.5 11.5 0 10l5-5 5 5z\"></path></svg></button>\n" +
        "          <button type=\"button\" class=\"btn btn-outline btn-sm show-on-focus sortable-button js-sortable-button right-0\" data-direction=\"down\"><svg aria-label=\"Move Unlucky down\" class=\"octicon octicon-chevron-down\" viewBox=\"0 0 10 16\" version=\"1.1\" width=\"10\" height=\"16\" role=\"img\"><path fill-rule=\"evenodd\" d=\"M5 11L0 6l1.5-1.5L5 8.25 8.5 4.5 10 6l-5 5z\"></path></svg></button>\n" +
        "      </div>\n" +
        "\n" +
        "\n" +
        "      <p class=\"pinned-item-desc text-gray text-small d-block mt-2 mb-3\">\n" +
        "        Java LibGDX Android 2D RPG Game\n" +
        "      </p>\n" +
        "\n" +
        "      <p class=\"mb-0 f6 text-gray\">\n" +
        "          <span class=\"d-inline-block mr-3\">\n" +
        "  <span class=\"repo-language-color\" style=\"background-color: #b07219\"></span>\n" +
        "  <span itemprop=\"programmingLanguage\">Java</span>\n" +
        "</span>\n" +
        "\n" +
        "          <a href=\"/mingli1/Unlucky/stargazers\" class=\"pinned-item-meta muted-link \">\n" +
        "            <svg aria-label=\"stars\" class=\"octicon octicon-star\" viewBox=\"0 0 14 16\" version=\"1.1\" width=\"14\" height=\"16\" role=\"img\"><path fill-rule=\"evenodd\" d=\"M14 6l-4.9-.64L7 1 4.9 5.36 0 6l3.6 3.26L2.67 14 7 11.67 11.33 14l-.93-4.74L14 6z\"></path></svg>\n" +
        "            58\n" +
        "          </a>\n" +
        "          <a href=\"/mingli1/Unlucky/network/members\" class=\"pinned-item-meta muted-link \">\n" +
        "            <svg aria-label=\"forks\" class=\"octicon octicon-repo-forked\" viewBox=\"0 0 10 16\" version=\"1.1\" width=\"10\" height=\"16\" role=\"img\"><path fill-rule=\"evenodd\" d=\"M8 1a1.993 1.993 0 00-1 3.72V6L5 8 3 6V4.72A1.993 1.993 0 002 1a1.993 1.993 0 00-1 3.72V6.5l3 3v1.78A1.993 1.993 0 005 15a1.993 1.993 0 001-3.72V9.5l3-3V4.72A1.993 1.993 0 008 1zM2 4.2C1.34 4.2.8 3.65.8 3c0-.65.55-1.2 1.2-1.2.65 0 1.2.55 1.2 1.2 0 .65-.55 1.2-1.2 1.2zm3 10c-.66 0-1.2-.55-1.2-1.2 0-.65.55-1.2 1.2-1.2.65 0 1.2.55 1.2 1.2 0 .65-.55 1.2-1.2 1.2zm3-10c-.66 0-1.2-.55-1.2-1.2 0-.65.55-1.2 1.2-1.2.65 0 1.2.55 1.2 1.2 0 .65-.55 1.2-1.2 1.2z\"></path></svg>\n" +
        "            19\n" +
        "          </a>\n" +
        "      </p>\n" +
        "    </div>\n" +
        "  </div>" +
        "f4 mb-2 text-normal\n" +
        "Popular repositories"

class PinnedReposHelperTest {

    private lateinit var pinnedReposHelper: PinnedReposHelper

    @Before
    fun setup() {
        pinnedReposHelper = PinnedReposHelper()
    }

    @Test
    fun shouldParseHtml() {
        val pair = pinnedReposHelper.parse(SAMPLE_RESPONSE)

        assertThat(pair.first).isEqualTo("Popular repositories")
        assertThat(pair.second[0].fullName).isEqualTo("mingli1/Unlucky")
        assertThat(pair.second[0].repoDescription).isEqualTo("Java LibGDX Android 2D RPG Game")
        assertThat(pair.second[0].repoLanguage).isEqualTo("Java")
        assertThat(pair.second[0].repoPushedAt).isEqualTo("58")
        assertThat(pair.second[0].repoCreatedAt).isEqualTo("19")
    }
}