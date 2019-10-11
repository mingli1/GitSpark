package com.gitspark.gitspark.ui.main.repo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gitspark.gitspark.model.Repo
import javax.inject.Inject

class RepoDetailSharedViewModel @Inject constructor() : ViewModel() {

    val repoData = MutableLiveData<Repo>()
}