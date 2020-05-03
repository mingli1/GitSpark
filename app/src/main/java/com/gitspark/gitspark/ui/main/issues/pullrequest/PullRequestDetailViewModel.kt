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
    private var prDataLoaded = false
    private var prData: PullRequest? = null

    fun fetchPullRequestData(username: String, repoName: String, pullNumber: Int) {
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
}