package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.service.UserService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.AuthUser
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper
) {

    fun getUser(token: String): Observable<UserResult> {
        return retrofitHelper.getRetrofit(token = token)
            .create(UserService::class.java)
            .getAuthenticatedUser()
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to obtain user data.") }
    }

    private fun getSuccess(user: AuthUser): UserResult = UserResult.UserSuccess(user)

    private fun getFailure(error: String): UserResult = UserResult.UserFailure(error)
}

sealed class UserResult {
    data class UserSuccess(val user: AuthUser) : UserResult()
    data class UserFailure(val error: String) : UserResult()
}