package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.service.ISSUES_PER_PAGE
import com.gitspark.gitspark.api.service.IssueService
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.Issue
import com.gitspark.gitspark.model.Page
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueRepository @Inject constructor(
    private val prefsHelper: PreferencesHelper,
    private val retrofitHelper: RetrofitHelper
) {

    fun getIssues(
        filter: String,
        state: String,
        page: Int,
        perPage: Int = ISSUES_PER_PAGE
    ): Observable<IssueResult<Page<Issue>>> {
        return getIssueService()
            .getIssues(filter, state, page, perPage)
            .map {
                getSuccess(it.toModel<Issue>().apply {
                    value = it.response.map { issue -> issue.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain issues.") }
    }

    private fun getIssueService() =
        retrofitHelper.getRetrofit(token = prefsHelper.getCachedToken())
            .create(IssueService::class.java)

    private fun <T> getSuccess(value: T): IssueResult<T> = IssueResult.Success(value)

    private fun <T> getFailure(error: String): IssueResult<T> = IssueResult.Failure(error)
}

sealed class IssueResult<T> {
    data class Success<T>(val value: T) : IssueResult<T>()
    data class Failure<T>(val error: String) : IssueResult<T>()
}