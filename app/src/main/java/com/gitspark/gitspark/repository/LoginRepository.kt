package com.gitspark.gitspark.repository

import android.net.Uri
import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.api.service.LoginService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.api.model.ApiAuthRequest
import com.gitspark.gitspark.api.model.ApiBadCredentials
import com.gitspark.gitspark.api.model.DEFAULT_AUTH
import com.gitspark.gitspark.model.AccessToken
import com.gitspark.gitspark.model.Token
import com.squareup.moshi.Moshi
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

private const val REDIRECT_URL = "gitspark://login"
private const val LOGIN_STATE = "com.gitspark"

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
                    val error = moshi.adapter(ApiBadCredentials::class.java)
                        .fromJson(errorResponse)?.message
                    getFailure<Token>("Failed to authenticate: $error")
                } ?: getFailure("Failed to authenticate.")
            }
    }

    fun deleteAuthorization(basicToken: String, authId: Long): Completable {
        return getLoginService(basicToken)
            .deleteAuthorization(authId)
    }

    fun getAuthorizationUri(scopes: List<String>) =
        Uri.Builder()
            .scheme("https")
            .authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .appendQueryParameter("redirect_uri", REDIRECT_URL)
            .appendQueryParameter("scope", scopes.joinToString(" "))
            .appendQueryParameter("state", LOGIN_STATE)
            .build()

    fun getAccessToken(code: String): Single<LoginResult<AccessToken>> {
        return retrofitHelper.getRetrofit(baseUrl = "https://github.com/login/oauth/").create(LoginService::class.java)
            .getAccessToken(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code, LOGIN_STATE, REDIRECT_URL)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure(it.message ?: "Failed to obtain access token.") }
    }

    private fun getLoginService(token: String? = null): LoginService {
        return retrofitHelper.getRetrofit(token = token).create(LoginService::class.java)
    }

    private fun getHttpExceptionString(throwable: Throwable): String? {
        if (throwable !is HttpException) return null
        return throwable.response()?.errorBody()?.string()
    }

    private fun <T> getSuccess(value: T): LoginResult<T> = LoginResult.Success(value)

    private fun <T> getFailure(error: String): LoginResult<T> = LoginResult.Failure(error)
}

sealed class LoginResult<T> {
    data class Success<T>(val value: T) : LoginResult<T>()
    data class Failure<T>(val error: String) : LoginResult<T>()
}