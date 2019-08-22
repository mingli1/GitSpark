package com.gitspark.gitspark.repository

import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.api.service.LoginService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.api.model.ApiAuthRequest
import com.gitspark.gitspark.api.model.ApiBadCredentials
import com.gitspark.gitspark.api.model.DEFAULT_AUTH
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.Token
import com.squareup.moshi.Moshi
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper,
    private val moshi: Moshi
) {

    fun putAuthorizations(basicToken: String, request: ApiAuthRequest = DEFAULT_AUTH): Observable<LoginResult<Token>> {
        return getLoginService(basicToken)
            .putAuthorizations(BuildConfig.GITHUB_CLIENT_ID, request)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn {
                getHttpExceptionString(it)?.let { errorResponse ->
                    val error = moshi.adapter<ApiBadCredentials>(ApiBadCredentials::class.java)
                        .fromJson(errorResponse)?.message
                    getFailure<Token>("Failed to authenticate: $error")
                } ?: getFailure("Failed to authenticate.")
            }
    }

    fun getAuthorizations(basicToken: String): Observable<LoginResult<Page<Token>>> {
        return getLoginService(basicToken)
            .getAuthorizations()
            .map {
                getSuccess(it.toModel<Token>().apply {
                    value = it.response.map { token -> token.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain authorizations.") }
    }

    fun deleteAuthorization(basicToken: String, authId: Int): Completable {
        return getLoginService(basicToken)
            .deleteAuthorization(authId)
    }

    private fun getLoginService(token: String? = null): LoginService {
        return retrofitHelper.getRetrofit(token = token).create(LoginService::class.java)
    }

    private fun getHttpExceptionString(throwable: Throwable): String? {
        return (throwable as HttpException).response()?.errorBody()?.string()
    }

    private fun <T> getSuccess(value: T): LoginResult<T> = LoginResult.Success(value)

    private fun <T> getFailure(error: String): LoginResult<T> = LoginResult.Failure(error)
}

sealed class LoginResult<T> {
    data class Success<T>(val value: T) : LoginResult<T>()
    data class Failure<T>(val error: String) : LoginResult<T>()
}