package com.gitspark.gitspark.ui.main.tab

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class RepoDetailViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<RepoDetailViewState>()

    fun loadRepoData(owner: String, repoName: String) {
        viewState.value = RepoDetailViewState(loading = true)
        subscribe(repoRepository.getReadme(owner, repoName)) {
            when (it) {
                is RepoResult.Success -> {
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        readmeUrl = it.value.downloadUrl
                    )
                }
                is RepoResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false)
                }
            }
        }
    }
}