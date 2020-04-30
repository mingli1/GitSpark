package com.gitspark.gitspark.ui.main.shared

import com.gitspark.gitspark.model.PullRequestFile
import com.gitspark.gitspark.repository.IssueRepository
import com.gitspark.gitspark.repository.IssueResult
import javax.inject.Inject

class FileListViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : ListViewModel<PullRequestFile>() {

    fun onStart(args: String) {
        if (args.isEmpty()) return
        this.args = args
        start()
    }

    override fun requestData() {
        val args = this.args.split("/")
        subscribe(issueRepository.getPullRequestFiles(args[0], args[1], args[2].toInt())) {
            when (it) {
                is IssueResult.Success -> onDataSuccess(it.value.value, it.value.last)
                is IssueResult.Failure -> onDataFailure(it.error)
            }
        }
    }
}