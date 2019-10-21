package com.gitspark.gitspark.ui.main.repo

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.api.model.ApiSubscribed
import com.gitspark.gitspark.model.Branch
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import java.util.*
import javax.inject.Inject

class RepoDetailViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel() {

    val loading = MutableLiveData<Boolean>()
    val branchesData = SingleLiveEvent<List<Branch>>()
    val watchingData = SingleLiveEvent<ApiSubscribed>()
    val numWatchersData = SingleLiveEvent<Int>()
    val starringData = SingleLiveEvent<Boolean>()
    val languagesData = SingleLiveEvent<SortedMap<String, Int>>()

    private var dataLoaded = false

    fun fetchAdditionalRepoData(repo: Repo) {
        if (!dataLoaded) {
            loading.value = true
            requestRepoBranches(repo.owner.login, repo.repoName)
            requestRepoActivityData(repo.owner.login, repo.repoName)
            requestNumWatchersData(repo.owner.login, repo.repoName)
            requestLanguagesData(repo.owner.login, repo.repoName)
            dataLoaded = true
        }
    }

    private fun requestRepoBranches(username: String, repoName: String) {
        subscribe(repoRepository.getBranches(username, repoName)) {
            when (it) {
                is RepoResult.Success -> branchesData.value = it.value.value
                is RepoResult.Failure -> alert(it.error)
            }
            loading.value = false
        }
    }

    private fun requestRepoActivityData(username: String, repoName: String) {
        subscribe(repoRepository.isWatchedByAuthUser(username, repoName)) {
            when (it) {
                is RepoResult.Success -> watchingData.value = it.value
                is RepoResult.Failure -> watchingData.value = ApiSubscribed(subscribed = false, ignored = false)
            }
        }
        subscribe(repoRepository.isStarredByAuthUser(username, repoName),
            { starringData.value = true },
            { starringData.value = false })
    }

    private fun requestNumWatchersData(username: String, repoName: String) {
        subscribe(repoRepository.getWatchers(username, repoName, 1, 1)) {
            when (it) {
                is RepoResult.Success -> {
                    val total = when {
                        it.value.last == -1 -> it.value.value.size
                        else -> it.value.last
                    }
                    numWatchersData.value = total
                }
                is RepoResult.Failure -> alert(it.error)
            }
        }
    }

    private fun requestLanguagesData(username: String, repoName: String) {
        subscribe(repoRepository.getLanguages(username, repoName)) {
            when (it) {
                is RepoResult.Success -> languagesData.value = it.value
                is RepoResult.Failure -> alert(it.error)
            }
        }
    }
}