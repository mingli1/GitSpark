package com.gitspark.gitspark.ui.main.tab

import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class RepoDetailViewModel @Inject constructor(
    private val repoRepository: RepoRepository
) : BaseViewModel() {


}