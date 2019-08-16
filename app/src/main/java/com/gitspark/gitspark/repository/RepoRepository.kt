package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.model.ApiAuthRepoRequest
import com.gitspark.gitspark.api.service.RepoService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.Repo
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper
) {

    fun getAuthRepos(
        token: String,
        request: ApiAuthRepoRequest = ApiAuthRepoRequest()
    ): Observable<RepoResult> {
        return retrofitHelper.getRetrofit(token = token)
            .create(RepoService::class.java)
            .getAuthRepos(request)
            .map { repos -> getSuccess(repos.map { it.toModel() }) }
            .onErrorReturn { getFailure("Failed to get authenticated user repositories.") }
    }

    private fun getSuccess(repos: List<Repo>): RepoResult = RepoResult.RepoSuccess(repos)

    private fun getFailure(error: String): RepoResult = RepoResult.RepoFailure(error)
}

sealed class RepoResult {
    data class RepoSuccess(val repos: List<Repo>) : RepoResult()
    data class RepoFailure(val error: String) : RepoResult()
}