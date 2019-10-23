package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.model.ApiAuthRepoRequest
import com.gitspark.gitspark.api.model.ApiSubscribed
import com.gitspark.gitspark.api.service.COMMITS_PER_PAGE
import com.gitspark.gitspark.api.service.REPO_PER_PAGE
import com.gitspark.gitspark.api.service.RepoService
import com.gitspark.gitspark.api.service.USER_PER_PAGE
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.*
import io.reactivex.Observable
import java.util.*
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

    fun getRawContent(url: String): Observable<RepoResult<String>> {
        return retrofitHelper.getRetrofit(lenient = true)
            .create(RepoService::class.java)
            .getRawContent(url)
            .map { getSuccess(it) }
            .onErrorReturn { getFailure("Failed to obtain raw content for $url") }
    }

    fun getReadme(
        username: String,
        repoName: String
    ): Observable<RepoResult<RepoContent>> {
        return getRepoService()
            .getReadme(username, repoName)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to get README content for $username/$repoName") }
    }

    fun getFile(
        username: String,
        repoName: String,
        path: String = "",
        ref: String
    ): Observable<RepoResult<RepoContent>> {
        return getRepoService()
            .getFile(username, repoName, path, ref)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to obtain file at path $path") }
    }

    fun getDirectory(
        username: String,
        repoName: String,
        path: String = "",
        ref: String
    ): Observable<RepoResult<Page<RepoContent>>> {
        return getRepoService()
            .getDirectory(username, repoName, path, ref)
            .map {
                getSuccess(it.toModel<RepoContent>().apply {
                    value = it.response.map { content -> content.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain directory at path $path") }
    }

    fun getBranches(
        username: String,
        repoName: String,
        page: Int = 1
    ): Observable<RepoResult<Page<Branch>>> {
        return getRepoService()
            .getBranches(username, repoName, page)
            .map {
                getSuccess(it.toModel<Branch>().apply {
                    value = it.response.map { branch -> branch.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain branches for $username/$repoName") }
    }

    fun isStarredByAuthUser(username: String, repoName: String) =
        getRepoService().isStarredByAuthUser(username, repoName)

    fun isWatchedByAuthUser(username: String, repoName: String): Observable<RepoResult<ApiSubscribed>> {
        return getRepoService()
            .isWatchedByAuthUser(username, repoName)
            .map { getSuccess(it) }
            .onErrorReturn { getFailure("User is not watching $repoName") }
    }

    fun watchRepo(username: String, repoName: String, subscribed: Boolean, ignored: Boolean) =
        getRepoService().watchRepo(username, repoName, subscribed, ignored)

    fun unwatchRepo(username: String, repoName: String) =
        getRepoService().unwatchRepo(username, repoName)

    fun starRepo(username: String, repoName: String) =
        getRepoService().starRepo(username, repoName)

    fun unstarRepo(username: String, repoName: String) =
        getRepoService().unstarRepo(username, repoName)

    fun forkRepo(username: String, repoName: String) =
        getRepoService().forkRepo(username, repoName)

    fun getWatchers(
        username: String,
        repoName: String,
        page: Int = 1,
        perPage: Int = USER_PER_PAGE
    ): Observable<RepoResult<Page<User>>> {
        return getRepoService()
            .getWatchers(username, repoName, page, perPage)
            .map {
                getSuccess(it.toModel<User>().apply {
                    value = it.response.map { user -> user.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain watchers for $username/$repoName") }
    }

    fun getStargazers(
        username: String,
        repoName: String,
        page: Int = 1,
        perPage: Int = USER_PER_PAGE
    ): Observable<RepoResult<Page<User>>> {
        return getRepoService()
            .getStargazers(username, repoName, page, perPage)
            .map {
                getSuccess(it.toModel<User>().apply {
                    value = it.response.map { user -> user.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain stargazers for $username/$repoName") }
    }

    fun getForks(
        username: String,
        repoName: String,
        page: Int = 1,
        perPage: Int = REPO_PER_PAGE
    ): Observable<RepoResult<Page<Repo>>> {
        return getRepoService()
            .getForks(username, repoName, page, perPage)
            .map {
                getSuccess(it.toModel<Repo>().apply {
                    value = it.response.map { repo -> repo.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain forks for $username/$repoName") }
    }

    fun getContributors(
        username: String,
        repoName: String,
        page: Int = 1,
        perPage: Int = USER_PER_PAGE
    ): Observable<RepoResult<Page<User>>> {
        return getRepoService()
            .getContributors(username, repoName, page, perPage)
            .map {
                getSuccess(it.toModel<User>().apply {
                    value = it.response.map { user -> user.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain contributors for $username/$repoName") }
    }

    fun getLanguages(username: String, repoName: String): Observable<RepoResult<SortedMap<String, Int>>> {
        return getRepoService()
            .getLanguages(username, repoName)
            .map {
                getSuccess(it.toSortedMap(kotlin.Comparator { o1, o2 ->
                    (it[o2] ?: 0) - (it[o1] ?: 0)
                }))
            }
            .onErrorReturn { getFailure("Failed to obtain languages for $username/$repoName") }
    }

    fun getCommits(
        username: String,
        repoName: String,
        sha: String,
        page: Int = 1,
        perPage: Int = COMMITS_PER_PAGE
    ): Observable<RepoResult<Page<Commit>>> {
        return getRepoService()
            .getCommits(username, repoName, sha, page, perPage)
            .map {
                getSuccess(it.toModel<Commit>().apply {
                    value = it.response.map { commit -> commit.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain commits for $username/$repoName") }
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