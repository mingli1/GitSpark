package com.gitspark.gitspark.ui.main.shared

import com.gitspark.gitspark.model.Commit
import com.gitspark.gitspark.repository.IssueRepository
import com.gitspark.gitspark.repository.IssueResult
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import javax.inject.Inject

enum class CommitListType {
    Repo,
    PullRequest
}

class CommitListViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val issueRepository: IssueRepository
) : ListViewModel<Commit>() {

    private var type = CommitListType.Repo

    fun onStart(type: CommitListType, args: String) {
        if (args.isEmpty()) return
        this.type = type
        this.args = args
        start()
    }

    override fun requestData() {
        when (type) {
            CommitListType.Repo -> requestRepoCommits()
            CommitListType.PullRequest -> requestIssueCommits()
        }
    }

    private fun requestRepoCommits() {
        val args = this.args.split("/")
        subscribe(repoRepository.getCommits(args[0], args[1], args[2], page = page)) {
            when (it) {
                is RepoResult.Success -> onDataSuccess(it.value.value, it.value.last)
                is RepoResult.Failure -> onDataFailure(it.error)
            }
        }
    }

    private fun requestIssueCommits() {
        val args = this.args.split("/")
        subscribe(issueRepository.getPullRequestCommits(args[0], args[1], args[2].toInt(), page = page)) {
            when (it) {
                is IssueResult.Success -> onDataSuccess(it.value.value, it.value.last)
                is IssueResult.Failure -> onDataFailure(it.error)
            }
        }
    }
}