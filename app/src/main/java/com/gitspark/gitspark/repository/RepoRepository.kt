package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.model.ApiAuthRepoRequest
import com.gitspark.gitspark.api.service.REPO_PER_PAGE
import com.gitspark.gitspark.api.service.RepoService
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.Repo
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper,
    private val prefsHelper: PreferencesHelper
) {

    fun getAuthRepos(
        request: ApiAuthRepoRequest = ApiAuthRepoRequest(),
        page: Int
    ): Observable<RepoResult<Page<Repo>>> {
        return getRepoService()
            .getAuthRepos(request.visibility, request.affiliation, request.sort, page)
            .map {
                getSuccess(it.toModel<Repo>().apply {
                    value = it.response.map { repo -> repo.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to get authenticated user repositories.") }
    }

    fun getRepos(
        username: String,
        request: ApiAuthRepoRequest = ApiAuthRepoRequest(),
        page: Int,
        perPage: Int = REPO_PER_PAGE
    ): Observable<RepoResult<Page<Repo>>> {
        return getRepoService()
            .getRepos(username, request.visibility, request.affiliation, request.sort, page, perPage)
            .map {
                getSuccess(it.toModel<Repo>().apply {
                    value = it.response.map { repo -> repo.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to get repository data for $username") }
    }

    fun getAuthStarredRepos(
        page: Int,
        perPage: Int = REPO_PER_PAGE
    ): Observable<RepoResult<Page<Repo>>> {
        return getRepoService()
            .getAuthStarredRepos(page, perPage)
            .map {
                getSuccess(it.toModel<Repo>().apply {
                    value = it.response.map { repo -> repo.toModel().apply { starred = true } }
                })
            }
            .onErrorReturn { getFailure("Failed to get authenticated starred repositories.") }
    }

    fun getStarredRepos(
        username: String,
        page: Int,
        perPage: Int = REPO_PER_PAGE
    ): Observable<RepoResult<Page<Repo>>> {
        return getRepoService()
            .getStarredRepos(username, page, perPage)
            .map {
                getSuccess(it.toModel<Repo>().apply {
                    value = it.response.map { repo -> repo.toModel().apply { starred = true } }
                })
            }
            .onErrorReturn { getFailure("Failed to get starred repositories for $username") }
    }

    private fun getRepoService() =
        retrofitHelper.getRetrofit(token = prefsHelper.getCachedToken())
            .create(RepoService::class.java)

    private fun <T> getSuccess(value: T): RepoResult<T> = RepoResult.Success(value)

    private fun <T> getFailure(error: String): RepoResult<T> = RepoResult.Failure(error)
}

sealed class RepoResult<T> {
    data class Success<T>(val value: T) : RepoResult<T>()
    data class Failure<T>(val error: String) : RepoResult<T>()
}