package com.gitspark.gitspark.repository

import androidx.lifecycle.LiveData
import com.gitspark.gitspark.api.model.ApiAuthRepoRequest
import com.gitspark.gitspark.api.model.SORT_CREATED
import com.gitspark.gitspark.api.model.SORT_FULL_NAME
import com.gitspark.gitspark.api.model.SORT_PUSHED
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
            .getAuthRepos(request.visibility, request.affiliation, request.sort)
            .map { repos -> getSuccess(repos.map { it.toModel() }) }
            .onErrorReturn { getFailure("Failed to get authenticated user repositories.") }
    }

    fun getAuthStarredRepos(token: String): Observable<RepoResult> {
        return retrofitHelper.getRetrofit(token = token)
            .create(RepoService::class.java)
            .getAuthStarredRepos()
            .map { starredRepos ->
                getSuccess(starredRepos.map { repo ->
                    repo.toModel().apply { starred = true }
                })
            }
            .onErrorReturn { getFailure("Failed to get authenticated starred repositories.") }
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
        search.trim()
        return when (order) {
            SORT_FULL_NAME ->
                if (isPrivate) repoDao.getPrivateReposDefaultOrder(search)
                else repoDao.getAllReposDefaultOrder(search)
            SORT_PUSHED ->
                if (isPrivate) repoDao.getPrivateReposOrderByPushed(search)
                else repoDao.getAllReposOrderByPushed(search)
            SORT_CREATED ->
                if (isPrivate) repoDao.getPrivateReposOrderByCreated(search)
                else repoDao.getAllReposOrderByCreated(search)
            else ->
                if (isPrivate) repoDao.getPrivateReposOrderByUpdated(search)
                else repoDao.getAllReposOrderByUpdated(search)
        }
    }

    fun isRepoCacheExpired(timestamp: String) =
        timeHelper.isExpiredMinutes(timeHelper.parse(timestamp), REPO_CACHE_DURATION_M)

    private fun getSuccess(repos: List<Repo>): RepoResult = RepoResult.Success(repos)

    private fun getFailure(error: String): RepoResult = RepoResult.Failure(error)
}

sealed class RepoResult {
    data class Success(val repos: List<Repo>) : RepoResult()
    data class Failure(val error: String) : RepoResult()
}