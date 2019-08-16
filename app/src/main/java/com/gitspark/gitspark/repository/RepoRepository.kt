package com.gitspark.gitspark.repository

import androidx.lifecycle.LiveData
import com.gitspark.gitspark.api.model.ApiAuthRepoRequest
import com.gitspark.gitspark.api.service.RepoService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Repo
import com.gitspark.gitspark.room.dao.RepoDao
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

private const val REPO_CACHE_DURATION_M = 30L

@Singleton
class RepoRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper,
    private val timeHelper: TimeHelper,
    private val repoDao: RepoDao
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

    fun cacheRepos(repos: List<Repo>): Completable {
        return Completable.fromAction {
            val timestamp = timeHelper.nowAsString()
            repos.forEach {
                it.timestamp = timestamp
                repoDao.insertRepo(it)
            }
        }
    }

    fun getRepos(
        search: String = "%",
        isPrivate: Boolean = false,
        order: String
    ): LiveData<List<Repo>> {
        return when (order) {
            "fullName" -> repoDao.getReposDefaultOrder(search, isPrivate)
            else -> repoDao.getReposOrderByDate(search, isPrivate, order)
        }
    }

    fun isRepoCacheExpired(timestamp: String) =
        timeHelper.isExpiredMinutes(timeHelper.parse(timestamp), REPO_CACHE_DURATION_M)

    private fun getSuccess(repos: List<Repo>): RepoResult = RepoResult.RepoSuccess(repos)

    private fun getFailure(error: String): RepoResult = RepoResult.RepoFailure(error)
}

sealed class RepoResult {
    data class RepoSuccess(val repos: List<Repo>) : RepoResult()
    data class RepoFailure(val error: String) : RepoResult()
}