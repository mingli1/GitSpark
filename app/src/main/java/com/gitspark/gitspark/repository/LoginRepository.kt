package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.service.LoginService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.api.model.ApiAuthRequest
import com.gitspark.gitspark.api.model.DEFAULT_AUTH
import com.gitspark.gitspark.model.Token
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper
) {

    fun login(token: String, auth: ApiAuthRequest = DEFAULT_AUTH): Observable<LoginResult> {
        return retrofitHelper.getRetrofit(token = token)
            .create(LoginService::class.java)
            .postAuthorizations(auth)
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure() }
    }

    private fun getSuccess(token: Token): LoginResult = LoginResult.Success(token)

    private fun getFailure(): LoginResult = LoginResult.Failure("Failed to authenticate")
}

sealed class LoginResult {
    data class Success(val token: Token) : LoginResult()
    data class Failure(val error: String) : LoginResult()
}