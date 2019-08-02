package com.gitspark.gitspark.repository

import android.util.Log
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

    fun postAuth(token: String, auth: ApiAuthRequest = DEFAULT_AUTH): Observable<LoginResult> {
        return retrofitHelper.getRetrofit(token = token)
            .create(LoginService::class.java)
            .postAuthorizations(auth)
            .map { getSuccess(it.toModel()) }
            .doOnError {
                Log.d(TAG, "${(it as HttpException).response()?.errorBody()?.string()}")
            }
            .onErrorReturn { getFailure() }
    }

    fun getAuthList(): Observable<List<Token>> {
        return retrofitHelper.getRetrofit()
            .create(LoginService::class.java)
            .getAuthorizations()
            .map { it.map { token ->
                Log.d(TAG, "Token value from: ${token}")
                token.toModel()
            } }
            .doOnError { Log.d(TAG, "Failed to list authorizations") }
    }

    fun deleteAuth(tokenId: Int): Observable<String> {
        return retrofitHelper.getRetrofit()
            .create(LoginService::class.java)
            .deleteAuthorization(tokenId)
            .map { it.string() }
            .doOnError { Log.d(TAG, "Failed to delete authorization id $tokenId") }
    }

    private fun getSuccess(token: Token): LoginResult = LoginResult.Success(token)

    private fun getFailure(): LoginResult = LoginResult.Failure("Failed to authenticate")
}

sealed class LoginResult {
    data class Success(val token: Token) : LoginResult()
    data class Failure(val error: String) : LoginResult()
}