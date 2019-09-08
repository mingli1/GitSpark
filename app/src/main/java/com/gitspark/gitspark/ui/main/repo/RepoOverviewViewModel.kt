package com.gitspark.gitspark.ui.main.repo

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.ui.base.BaseViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class RepoOverviewViewModel @Inject constructor(
    private val colorHelper: LanguageColorHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<RepoOverviewViewState>()

    fun loadRepo(repo: Repo) {
        with (repo) {
            var updatedText = ""
            if (repoPushedAt.isNotEmpty()) {
                val updatedDate = Instant.parse(repoPushedAt)
                val dateTime = LocalDateTime.ofInstant(
                    updatedDate,
                    ZoneOffset.UTC
                )
                updatedText = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss").format(dateTime)
            }

            viewState.value = RepoOverviewViewState(
                repoName = repoName,
                repoDescription = repoDescription,
                topics = topics,
                isArchived = archived,
                isPrivate = isPrivate,
                isForked = isForked,
                repoLanguage = repoLanguage,
                languageColor = colorHelper.getColor(repoLanguage) ?: -1,
                updatedText = updatedText,
                licenseText = license.licenseName ?: "",
                numWatchers = numWatches,
                numStars = numStars,
                numForks = numForks,
                readmeUrl = "" // TODO make call for readme
            )
        }
    }
}