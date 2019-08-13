package com.gitspark.gitspark.repository

import androidx.lifecycle.LiveData
import com.gitspark.gitspark.api.service.UserService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.room.dao.AuthUserDao
import io.reactivex.Completable
import io.reactivex.Observable
import org.threeten.bp.Instant
import javax.inject.Inject
import javax.inject.Singleton

private const val USER_CACHE_DURATION_M = 30L

@Singleton
class UserRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper,
    private val authUserDao: AuthUserDao,
    private val timeHelper: TimeHelper
) {

    fun getAuthUser(token: String): Observable<UserResult> {
        return retrofitHelper.getRetrofit(token = token)
            .create(UserService::class.java)
            .getAuthenticatedUser()
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to obtain user data.") }
    }

    fun cacheUserData(user: AuthUser): Completable {
        return Completable.fromAction {
            user.timestamp = timeHelper.now()
            authUserDao.insertAuthUser(user)
        }
    }

    fun isUserCacheExpired(timestamp: Instant) =
        timeHelper.isExpiredMinutes(timestamp, USER_CACHE_DURATION_M)

    fun getCurrentUserData(): LiveData<AuthUser> = authUserDao.getAuthUser()

    private fun getSuccess(user: AuthUser): UserResult = UserResult.Success(user)

    private fun getFailure(error: String): UserResult = UserResult.Failure(error)
}

sealed class UserResult {
    data class Success(val user: AuthUser) : UserResult()
    data class Failure(val error: String) : UserResult()
}