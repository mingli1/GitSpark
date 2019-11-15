package com.gitspark.gitspark.ui.main.shared

import com.gitspark.gitspark.model.Commit
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import javax.inject.Inject

class CommitListViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : ListViewModel<Commit>() {

    fun onStart(args: String) {
        if (args.isEmpty()) return
        this.args = args
        start()
    }

    override fun requestData() {
        val args = this.args.split("/")
        subscribe(repoRepository.getCommits(args[0], args[1], args[2], page = page)) {
            when (it) {
                is RepoResult.Success -> onDataSuccess(it.value.value, it.value.last)
                is RepoResult.Failure -> onDataFailure(it.error)
            }
        }
    }
}