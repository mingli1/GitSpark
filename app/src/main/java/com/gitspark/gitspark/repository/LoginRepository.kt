package com.gitspark.gitspark.repository

import android.net.Uri
import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.api.service.LoginService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.AccessToken
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

private const val LOGIN_STATE = "com.gitspark"

@Singleton
class LoginRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper
) {

    fun getAuthorizationUri(): Uri =
        Uri.Builder()
            .scheme("https")
            .authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .appendQueryParameter("redirect_uri", BuildConfig.CALLBACK_URL)
            .appendQueryParameter("scope", "user,repo,gist,notifications,read:org")
            .appendQueryParameter("state", LOGIN_STATE)
            .build()

    fun getAccessToken(code: String): Single<LoginResult<AccessToken>> {
        return retrofitHelper.getRetrofit(baseUrl = "https://github.com/login/oauth/").create(LoginService::class.java)
            .getAccessToken(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code, LOGIN_STATE, BuildConfig.CALLBACK_URL)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure(it.message ?: "Failed to obtain access token.") }
    }

    private fun <T> getSuccess(value: T): LoginResult<T> = LoginResult.Success(value)

    private fun <T> getFailure(error: String): LoginResult<T> = LoginResult.Failure(error)
}

sealed class LoginResult<T> {
    data class Success<T>(val value: T) : LoginResult<T>()
    data class Failure<T>(val error: String) : LoginResult<T>()
}