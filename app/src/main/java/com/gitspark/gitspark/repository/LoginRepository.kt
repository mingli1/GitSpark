package com.gitspark.gitspark.repository

import android.util.Log
import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.api.service.LoginService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.api.model.ApiAuthRequest
import com.gitspark.gitspark.api.model.DEFAULT_AUTH
import com.gitspark.gitspark.model.Token
import io.reactivex.Observable
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "LoginRepository"

@Singleton
class LoginRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper
) {

    fun putAuth(basicToken: String, request: ApiAuthRequest = DEFAULT_AUTH): Observable<LoginResult> {
        return retrofitHelper.getRetrofit(token = basicToken)
            .create(LoginService::class.java)
            .putAuthorizations(BuildConfig.GITHUB_CLIENT_ID, request)
            .map { getSuccess(it.toModel()) }
            .doOnError { Log.d(TAG, "${(it as HttpException).response()?.errorBody()?.string()}") }
            .onErrorReturn { getFailure((it as HttpException).response()?.errorBody()?.string()) }
    }

    private fun getSuccess(token: Token): LoginResult = LoginResult.Success(token)

    private fun getFailure(error: String?): LoginResult = LoginResult.Failure(error)
}

sealed class LoginResult {
    data class Success(val token: Token) : LoginResult()
    data class Failure(val error: String?) : LoginResult()
}