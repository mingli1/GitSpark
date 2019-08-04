package com.gitspark.gitspark.repository

import android.util.Log
import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.api.service.LoginService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.api.model.ApiAuthRequest
import com.gitspark.gitspark.api.model.DEFAULT_AUTH
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.PREFERENCES_TOKEN
import com.gitspark.gitspark.model.Token
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "LoginRepository"

@Singleton
class LoginRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper,
    private val preferencesHelper: PreferencesHelper
) {

    fun putAuthorizations(basicToken: String, request: ApiAuthRequest = DEFAULT_AUTH): Observable<LoginResult> {
        return getLoginService(basicToken)
            .putAuthorizations(BuildConfig.GITHUB_CLIENT_ID, request)
            .map { getSuccess(it.toModel()) }
            .doOnError { logHttpException(it) }
            .onErrorReturn { getFailure(getHttpExceptionString(it)) }
    }

    fun getAuthorizations(basicToken: String): Observable<List<Token>> {
        return getLoginService(basicToken)
            .getAuthorizations()
            .map { it.map { token -> token.toModel() } }
            .doOnError { Log.d(TAG, "${it.message}") }
            .onErrorReturn { emptyList() }
    }

    fun deleteAuthorization(basicToken: String, authId: Int): Completable {
        return getLoginService(basicToken)
            .deleteAuthorization(authId)
    }

    fun cacheAccessToken(token: Token) {
        preferencesHelper.saveString(PREFERENCES_TOKEN, token.value)
        preferencesHelper.saveString(token.hashedValue, token.value)
    }

    fun isTokenCached(token: Token) = preferencesHelper.contains(token.hashedValue)

    private fun getLoginService(token: String? = null): LoginService {
        return retrofitHelper.getRetrofit(token = token).create(LoginService::class.java)
    }

    private fun logHttpException(throwable: Throwable) {
        Log.d(TAG, getHttpExceptionString(throwable))
    }

    private fun getHttpExceptionString(throwable: Throwable): String? {
        return (throwable as HttpException).response()?.errorBody()?.string()
    }

    private fun getSuccess(token: Token): LoginResult = LoginResult.Success(token)

    private fun getFailure(error: String?): LoginResult = LoginResult.Failure(error)
}

sealed class LoginResult {
    data class Success(val token: Token) : LoginResult()
    data class Failure(val error: String?) : LoginResult()
}