package com.gitspark.gitspark.ui.main.repo

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.api.model.ApiSubscribed
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import com.gitspark.gitspark.ui.main.shared.RepoListType
import com.gitspark.gitspark.ui.main.shared.UserListType
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
    val updatedRepoData = SingleLiveEvent<Repo>()
    val forkButtonAction = SingleLiveEvent<String>()
    val navigateToUserListAction = SingleLiveEvent<Triple<String, UserListType, String>>()
    val navigateToRepoListAction = SingleLiveEvent<Triple<String, RepoListType, String>>()

    private lateinit var repo: Repo
    @VisibleForTesting var userWatching = false
    @VisibleForTesting var userStarred = false

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
                numStars = numStars,
                numForks = numForks,
                loading = true
            )
        }

        requestRepoReadme(repo.owner.login, repo.repoName)
    }

    fun setUserWatching(watchData: ApiSubscribed) {
        userWatching = watchData.subscribed
        viewState.value = viewState.value?.copy(userWatching = userWatching)
            ?: RepoOverviewViewState(userWatching = userWatching)
    }

    fun setUserStarring(starring: Boolean) {
        userStarred = starring
        viewState.value = viewState.value?.copy(userStarring = starring)
            ?: RepoOverviewViewState(userStarring = starring)
    }

    fun setNumWatching(numWatchers: Int) {
        viewState.value = viewState.value?.copy(numWatchers = numWatchers)
            ?: RepoOverviewViewState(numWatchers = numWatchers)
    }

    fun onWatchButtonClicked() {
        when {
            userWatching -> requestUnwatchRepo()
            else -> requestWatchRepo()
        }
        userWatching = !userWatching
    }

    fun onStarButtonClicked() {
        if (userStarred) {
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
        userStarred = !userStarred
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

    fun onUserListClicked(title: String, type: UserListType) {
        navigateToUserListAction.value = Triple(title, type, repo.fullName)
    }

    fun onRepoListClicked(title: String, type: RepoListType) {
        navigateToRepoListAction.value = Triple(title, type, repo.fullName)
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

    private fun requestWatchRepo() {
        subscribe(repoRepository.watchRepo(repo.owner.login, repo.repoName, subscribed = true, ignored = false),
            {
                val newWatchers = (viewState.value?.numWatchers ?: 0) + 1
                viewState.value = viewState.value?.copy(numWatchers = newWatchers, userWatching = true)
                updatedRepoData.value = repo.copy(numWatches = newWatchers)
            },
            { alert("Failed to watch repository") }
        )
    }

    private fun requestUnwatchRepo() {
        subscribe(repoRepository.unwatchRepo(repo.owner.login, repo.repoName),
            {
                val newWatchers = (viewState.value?.numWatchers ?: 0) - 1
                viewState.value = viewState.value?.copy(numWatchers = newWatchers, userWatching = false)
                updatedRepoData.value = repo.copy(numWatches = newWatchers)
            },
            { alert("Failed to unwatch repository") }
        )
    }
}