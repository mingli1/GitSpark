package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.service.SearchService
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.*
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val prefsHelper: PreferencesHelper,
    private val retrofitHelper: RetrofitHelper
) {

    fun searchRepos(
        query: String,
        page: Int,
        sort: String = "best_match",
        order: String = "desc"
    ): Observable<SearchResult<Page<Repo>>> {
        return getSearchService()
            .searchRepos(query, page, sort, order)
            .map {
                getSuccess(it.toModel<Repo>().apply {
                    value = it.response.map { repo -> repo.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain repo search results for query $query") }
    }

    fun searchUsers(
        query: String,
        page: Int,
        sort: String = "best_match",
        order: String = "desc"
    ): Observable<SearchResult<Page<User>>> {
        return getSearchService()
            .searchUsers(query, page, sort, order)
            .map {
                getSuccess(it.toModel<User>().apply {
                    value = it.response.map { user -> user.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain user search results for query $query") }
    }

    fun searchCode(
        query: String,
        page: Int,
        sort: String = "best_match",
        order: String = "desc"
    ): Observable<SearchResult<Page<File>>> {
        return getSearchService()
            .searchCode(query, page, sort, order)
            .map {
                getSuccess(it.toModel<File>().apply {
                    value = it.response.map { file-> file.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain code search results for query $query") }
    }

    fun searchCommits(
        query: String,
        page: Int,
        sort: String = "best_match",
        order: String = "desc"
    ): Observable<SearchResult<Page<Commit>>> {
        return getSearchService()
            .searchCommits(query, page, sort, order)
            .map {
                getSuccess(it.toModel<Commit>().apply {
                    value = it.response.map { commit -> commit.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain commit search results for query $query") }
    }

    fun searchIssues(
        query: String,
        page: Int,
        sort: String = "best_match",
        order: String = "desc"
    ): Observable<SearchResult<Page<Issue>>> {
        return getSearchService()
            .searchIssues(query, page, sort, order)
            .map {
                getSuccess(it.toModel<Issue>().apply {
                    value = it.response.map { issue -> issue.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain issue search results for query $query") }
    }

    private fun getSearchService() =
        retrofitHelper.getRetrofit(token = prefsHelper.getCachedToken())
            .create(SearchService::class.java)

    private fun <T> getSuccess(value: T): SearchResult<T> = SearchResult.Success(value)

    private fun <T> getFailure(error: String): SearchResult<T> = SearchResult.Failure(error)
}

sealed class SearchResult<T> {
    data class Success<T>(val value: T) : SearchResult<T>()
    data class Failure<T>(val error: String) : SearchResult<T>()
}