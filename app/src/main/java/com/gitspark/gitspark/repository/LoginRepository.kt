package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.model.ApiToken
import com.gitspark.gitspark.api.service.LoginService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.models.Authorization
import com.gitspark.gitspark.models.DEFAULT_AUTH
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper
) {

    fun login(token: String, auth: Authorization = DEFAULT_AUTH): Observable<LoginResult> {
        return retrofitHelper.getRetrofit(token = token)
            .create(LoginService::class.java)
            .postAuthorizations(auth)
            .map { getSuccess(it) }
            .onErrorReturn { getFailure() }
    }

    private fun getSuccess(token: ApiToken): LoginResult = LoginResult.Success(token)

    private fun getFailure(): LoginResult = LoginResult.Failure("Failed to authenticate")
}

sealed class LoginResult {
    data class Success(val token: ApiToken) : LoginResult()
    data class Failure(val error: String) : LoginResult()
}