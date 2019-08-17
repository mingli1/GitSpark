package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MediatorLiveData
import com.gitspark.gitspark.api.model.SORT_PUSHED
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class ReposViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel() {

    val repoDataMediator = MediatorLiveData<List<Repo>>()
    val repoDataAction = SingleLiveEvent<List<Repo>>()

    fun onResume() {
        val repoData = repoRepository.getRepos(order = SORT_PUSHED)
        repoDataMediator.addSource(repoData) { repoDataMediator.value = it }
    }

    fun onCachedRepoDataRetrieved(repos: List<Repo>) {
        if (repos.isEmpty()) return // TODO: handle empty case
        //val expired = repoRepository.isRepoCacheExpired(repos[0].timestamp)
        val expired = false
        if (expired) {

        }
        else repoDataAction.value = repos
    }
}