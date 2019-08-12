package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.service.UserService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.room.dao.AuthUserDao
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper,
    private val authUserDao: AuthUserDao
) {

    fun getAuthUser(token: String): Observable<UserResult> {
        return retrofitHelper.getRetrofit(token = token)
            .create(UserService::class.java)
            .getAuthenticatedUser()
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to obtain user data.") }
    }

    fun cacheUserData(user: AuthUser) = authUserDao.insertAuthUser(user)

    fun getCurrentUserData() = authUserDao.getAuthUser()

    private fun getSuccess(user: AuthUser): UserResult = UserResult.Success(user)

    private fun getFailure(error: String): UserResult = UserResult.Failure(error)
}

sealed class UserResult {
    data class Success(val user: AuthUser) : UserResult()
    data class Failure(val error: String) : UserResult()
}