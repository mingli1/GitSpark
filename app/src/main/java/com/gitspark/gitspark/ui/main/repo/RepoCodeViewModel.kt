package com.gitspark.gitspark.ui.main.repo

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.model.RepoContent
import com.gitspark.gitspark.model.TYPE_DIRECTORY
import com.gitspark.gitspark.model.TYPE_FILE
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class RepoCodeViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<RepoCodeViewState>()

    fun fetchContentsForBranch(repo: Repo, branchName: String) {
        viewState.value = viewState.value?.copy(loading = true, updateContent = false) ?:
                RepoCodeViewState(loading = true)
        subscribe(repoRepository.getDirectory(repo.owner.login, repo.repoName, ref = branchName)) {
            when (it) {
                is RepoResult.Success -> {
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        updateContent = true,
                        contentData = orderContents(it.value.value)
                    )
                }
                is RepoResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(loading = false)
                }
            }
        }
    }

    private fun orderContents(contents: List<RepoContent>): List<RepoContent> {
        val ordered = mutableListOf<RepoContent>()
        contents.forEach { if (it.type == TYPE_DIRECTORY) ordered.add(it) }
        contents.forEach { if (it.type != TYPE_DIRECTORY) ordered.add(it) }
        return ordered
    }
}