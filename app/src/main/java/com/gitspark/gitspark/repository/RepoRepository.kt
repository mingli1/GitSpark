package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.model.ApiAuthRepoRequest
import com.gitspark.gitspark.api.service.RepoService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.Page
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
        request: ApiAuthRepoRequest = ApiAuthRepoRequest(),
        page: Int
    ): Observable<RepoResult<Page<Repo>>> {
        return retrofitHelper.getRetrofit(token = token)
            .create(RepoService::class.java)
            .getAuthRepos(request.visibility, request.affiliation, request.sort, page)
            .map {
                getSuccess(it.toModel<Repo>().apply {
                    value = it.response.map { repo -> repo.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to get authenticated user repositories.") }
    }

    fun getAuthStarredRepos(token: String, page: Int): Observable<RepoResult<Page<Repo>>> {
        return retrofitHelper.getRetrofit(token = token)
            .create(RepoService::class.java)
            .getAuthStarredRepos(page)
            .map {
                getSuccess(it.toModel<Repo>().apply {
                    value = it.response.map { repo -> repo.toModel().apply { starred = true } }
                })
            }
            .onErrorReturn { getFailure("Failed to get authenticated starred repositories.") }
    }

    private fun <T> getSuccess(value: T): RepoResult<T> = RepoResult.Success(value)

    private fun <T> getFailure(error: String): RepoResult<T> = RepoResult.Failure(error)
}

sealed class RepoResult<T> {
    data class Success<T>(val value: T) : RepoResult<T>()
    data class Failure<T>(val error: String) : RepoResult<T>()
}