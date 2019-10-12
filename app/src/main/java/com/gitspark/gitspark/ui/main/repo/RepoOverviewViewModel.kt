package com.gitspark.gitspark.ui.main.repo

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.api.model.ApiSubscribed
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

internal const val NOT_WATCHING = 0
internal const val WATCHING = 1
internal const val IGNORING = 2

class RepoOverviewViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val colorHelper: LanguageColorHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<RepoOverviewViewState>()
    val updatedRepoData = SingleLiveEvent<Repo>()
    val watchButtonAction = SingleLiveEvent<String>()
    val forkButtonAction = SingleLiveEvent<String>()

    private lateinit var repo: Repo
    private var userWatching = NOT_WATCHING
    private var userStarring = false

    fun loadRepo(repo: Repo) {
        this.repo = repo
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

    fun setUserWatching(watchData: ApiSubscribed) {
        userWatching = when {
            watchData.subscribed -> WATCHING
            watchData.ignored -> IGNORING
            else -> NOT_WATCHING
        }
        viewState.value = viewState.value?.copy(userWatching = userWatching)
            ?: RepoOverviewViewState(userWatching = userWatching)
    }

    fun setUserStarring(starring: Boolean) {
        userStarring = starring
        viewState.value = viewState.value?.copy(userStarring = starring)
            ?: RepoOverviewViewState(userStarring = starring)
    }

    fun onWatchButtonClicked() {
        watchButtonAction.value = repo.fullName
    }

    fun onWatchItemSelected(which: Int) {
        if (userWatching == which) return
        when (which) {
            NOT_WATCHING ->
                requestUnwatchRepo(repo.owner.login, repo.repoName)
            WATCHING ->
                requestWatchRepo(repo.owner.login, repo.repoName, subscribed = true, ignored = false)
            else ->
                requestWatchRepo(repo.owner.login, repo.repoName, subscribed = false, ignored = true)
        }
        userWatching = which
    }

    fun onStarButtonClicked() {
        if (userStarring) {
            subscribe(repoRepository.unstarRepo(repo.owner.login, repo.repoName),
                {
                    val newStars = (viewState.value?.numStars ?: 0) - 1
                    viewState.value = viewState.value?.copy(
                        numStars = newStars,
                        userStarring = false
                    )
                    updatedRepoData.value = repo.copy(numStars = newStars)
                },
                { alert("Failed to unstar repository.") }
            )
        }
        else {
            subscribe(repoRepository.starRepo(repo.owner.login, repo.repoName),
                {
                    val newStars = (viewState.value?.numStars ?: 0) + 1
                    viewState.value = viewState.value?.copy(
                        numStars = newStars,
                        userStarring = true
                    )
                    updatedRepoData.value = repo.copy(numStars = newStars)
                },
                { alert("Failed to star repository") }
            )
        }
        userStarring = !userStarring
    }

    fun onForkButtonClicked() {
        forkButtonAction.value = repo.fullName
    }

    fun onForkConfirmClicked() {
        subscribe(repoRepository.forkRepo(repo.owner.login, repo.repoName),
            {
                val newForks = (viewState.value?.numForks ?: 0) + 1
                viewState.value = viewState.value?.copy(numForks = newForks)
                updatedRepoData.value = repo.copy(numForks = newForks)
            },
            { alert("Failed to fork repository") }
        )
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

    private fun requestWatchRepo(username: String, repoName: String, subscribed: Boolean, ignored: Boolean) {
        subscribe(repoRepository.watchRepo(username, repoName, subscribed, ignored),
            {
                when {
                    subscribed -> {
                        val newWatchers = (viewState.value?.numWatchers ?: 0) + 1
                        viewState.value = viewState.value?.copy(numWatchers = newWatchers, userWatching = WATCHING)
                        updatedRepoData.value = repo.copy(numWatches = newWatchers)
                    }
                    ignored -> {
                        viewState.value = viewState.value?.copy(userWatching = IGNORING)
                    }
                }
            },
            { alert("Failed to watch repository") }
        )
    }

    private fun requestUnwatchRepo(username: String, repoName: String) {
        subscribe(repoRepository.unwatchRepo(username, repoName),
            {
                val newWatchers = (viewState.value?.numWatchers ?: 0) - 1
                viewState.value = viewState.value?.copy(numWatchers = newWatchers, userWatching = NOT_WATCHING)
                updatedRepoData.value = repo.copy(numWatches = newWatchers)
            },
            { alert("Failed to unwatch repository") }
        )
    }
}