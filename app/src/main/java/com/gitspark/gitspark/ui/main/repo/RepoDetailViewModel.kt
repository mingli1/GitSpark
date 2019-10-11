package com.gitspark.gitspark.ui.main.repo

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.Branch
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class RepoDetailViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel() {

    val loading = MutableLiveData<Boolean>()
    val branchesData = SingleLiveEvent<List<Branch>>()
    val watchingData = SingleLiveEvent<Boolean>()
    val starringData = SingleLiveEvent<Boolean>()
    private var dataLoaded = false

    fun fetchAdditionalRepoData(repo: Repo) {
        if (!dataLoaded) {
            loading.value = true
            requestRepoBranches(repo.owner.login, repo.repoName)
            requestRepoActivityData(repo.owner.login, repo.repoName)
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
        subscribe(repoRepository.isWatchedByAuthUser(username, repoName),
            { watchingData.value = true },
            { watchingData.value = false })
        subscribe(repoRepository.isStarredByAuthUser(username, repoName),
            { starringData.value = true },
            { starringData.value = false })
    }
}