package com.gitspark.gitspark.ui.main.repo

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class RepoOverviewViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val colorHelper: LanguageColorHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<RepoOverviewViewState>()
    private var userWatching = false
    private var userStarring = false

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

            viewState.value = viewState.value?.copy(
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
                loading = true
            ) ?: RepoOverviewViewState(
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
                loading = true
            )
        }

        requestRepoReadme(repo.owner.login, repo.repoName)
    }

    fun setUserWatching(watching: Boolean) {
        userWatching = watching
        viewState.value = viewState.value?.copy(userWatching = watching)
            ?: RepoOverviewViewState(userWatching = watching)
    }

    fun setUserStarring(starring: Boolean) {
        userStarring = starring
        viewState.value = viewState.value?.copy(userStarring = starring)
            ?: RepoOverviewViewState(userStarring = starring)
    }

    fun onWatchButtonClicked() {

    }

    fun onStarButtonClicked() {

    }

    fun onForkButtonClicked() {

    }

    private fun requestRepoReadme(owner: String, repoName: String) {
        subscribe(repoRepository.getReadme(owner, repoName)) {
            when (it) {
                is RepoResult.Success -> {
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        readmeUrl = it.value.downloadUrl
                    )
                }
                is RepoResult.Failure ->
                    viewState.value = viewState.value?.copy(loading = false)
            }
        }
    }
}