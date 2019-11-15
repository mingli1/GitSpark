package com.gitspark.gitspark.ui.main.shared

import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import com.gitspark.gitspark.ui.nav.RepoDetailNavigator
import javax.inject.Inject

enum class RepoListType {
    None,
    Forks
}

class RepoListViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : ListViewModel<Repo>(), RepoDetailNavigator {

    val navigateToRepoDetailAction = SingleLiveEvent<Repo>()
    private var type = RepoListType.None

    fun onStart(type: RepoListType, args: String) {
        if (type == RepoListType.None|| args.isEmpty()) return
        this.type = type
        this.args = args
        start()
    }

    override fun onRepoSelected(repo: Repo) {
        navigateToRepoDetailAction.value = repo
    }

    override fun requestData() {
        when (type) {
            RepoListType.None-> return
            RepoListType.Forks -> requestForks()
        }
    }

    private fun requestForks() {
        val args = this.args.split("/")
        subscribe(repoRepository.getForks(args[0], args[1], page = page)) {
            onRepoDataResult(it)
        }
    }

    private fun onRepoDataResult(it: RepoResult<Page<Repo>>) {
        when (it) {
            is RepoResult.Success -> onDataSuccess(it.value.value, it.value.last)
            is RepoResult.Failure -> onDataFailure(it.error)
        }
    }
}