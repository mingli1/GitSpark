package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.service.ChecksService
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.Check
import com.gitspark.gitspark.model.CombinedRepoStatus
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChecksRepository @Inject constructor(
    private val prefsHelper: PreferencesHelper,
    private val retrofitHelper: RetrofitHelper
) {

    fun getCheckSuites(
        username: String,
        repoName: String,
        ref: String
    ): Single<ChecksResult<Check>> {
        return getChecksService()
            .getCheckSuites(username, repoName, ref)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to obtain check suites.") }
    }

    fun getCombinedStatus(
        username: String,
        repoName: String,
        ref: String
    ): Single<ChecksResult<CombinedRepoStatus>> {
        return getChecksService()
            .getCombinedStatus(username, repoName, ref)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to obtain checks status.") }
    }

    private fun getChecksService() =
        retrofitHelper.getRetrofit(token = prefsHelper.getCachedToken())
            .create(ChecksService::class.java)

    private fun <T> getSuccess(value: T): ChecksResult<T> = ChecksResult.Success(value)

    private fun <T> getFailure(error: String): ChecksResult<T> = ChecksResult.Failure(error)
}

sealed class ChecksResult<T> {
    data class Success<T>(val value: T) : ChecksResult<T>()
    data class Failure<T>(val error: String) : ChecksResult<T>()
}