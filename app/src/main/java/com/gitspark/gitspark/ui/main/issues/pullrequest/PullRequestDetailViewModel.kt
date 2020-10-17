package com.gitspark.gitspark.ui.main.issues.pullrequest

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.PullRequest
import com.gitspark.gitspark.repository.IssueRepository
import com.gitspark.gitspark.repository.IssueResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class PullRequestDetailViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : BaseViewModel() {

    val prDataRetrieved = SingleLiveEvent<PullRequest>()
    val loading = MutableLiveData<Boolean>()
    val exitFragment = SingleLiveAction()
    val navigateToEdit = SingleLiveEvent<Triple<String, PullRequest, String>>()
    val navigateToRepo = SingleLiveEvent<String>()

    private var prDataLoaded = false
    private var prData: PullRequest? = null

    private var username = ""
    private var repoName = ""
    private var pullNumber = 0

    fun fetchPullRequestData(username: String, repoName: String, pullNumber: Int) {
        this.username = username
        this.repoName = repoName
        this.pullNumber = pullNumber

        if (!prDataLoaded) {
            loading.value = true

            subscribe(issueRepository.getPullRequest(username, repoName, pullNumber)) {
                when (it) {
                    is IssueResult.Success -> {
                        prData = it.value
                        prDataRetrieved.value = it.value
                    }
                    is IssueResult.Failure -> {
                        alert(it.error)
                        exitFragment.call()
                    }
                }
                loading.value = false
            }
            prDataLoaded = true
        }
        else prDataRetrieved.value = prData
    }

    fun onEditSelected() {
        prData?.let {
            navigateToEdit.value = Triple(
                "Editing $username/$repoName #$pullNumber",
                it,
                "$username/$repoName"
            )
        }
    }

    fun onRepoSelected() {
        navigateToRepo.value = "$username/$repoName"
    }
}