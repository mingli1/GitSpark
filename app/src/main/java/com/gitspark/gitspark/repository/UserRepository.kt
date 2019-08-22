package com.gitspark.gitspark.repository

import androidx.lifecycle.LiveData
import com.gitspark.gitspark.api.service.UserService
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.room.dao.AuthUserDao
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

private const val USER_CACHE_DURATION_M = 30L
private const val CONTRIBUTIONS_URL = "https://github.com/users/%s/contributions"

@Singleton
class UserRepository @Inject constructor(
    private val retrofitHelper: RetrofitHelper,
    private val authUserDao: AuthUserDao,
    private val timeHelper: TimeHelper
) {

    fun getAuthUser(token: String): Observable<UserResult<AuthUser>> {
        return retrofitHelper.getRetrofit(token = token)
            .create(UserService::class.java)
            .getAuthenticatedUser()
            .map { getSuccess(it.toModel()) }
            .onErrorReturn { getFailure("Failed to obtain auth user data.") }
    }

    fun getUserFollowers(
        token: String,
        username: String,
        page: Int
    ): Observable<UserResult<Page<User>>> {
        return retrofitHelper.getRetrofit(token = token)
            .create(UserService::class.java)
            .getUserFollowers(username, page)
            .map { getSuccess(
                it.toModel<User>().apply {
                    value = it.response.map { user -> user.toModel() }
                })
            }
            .doOnError { println("$it") }
            .onErrorReturn { getFailure("Failed to obtain user followers.") }
    }

    fun getContributionsSvg(username: String): Observable<UserResult<String>> {
        return retrofitHelper.getRetrofit(lenient = true)
            .create(UserService::class.java)
            .getContributionsSvg(String.format(CONTRIBUTIONS_URL, username))
            .map { getSuccess(it) }
            .onErrorReturn { getFailure("Failed to obtain contributions data.") }
    }

    fun cacheUserData(user: AuthUser): Completable {
        return Completable.fromAction {
            user.timestamp = timeHelper.nowAsString()
            authUserDao.insertAuthUser(user)
        }
    }

    fun isUserCacheExpired(timestamp: String) =
        timeHelper.isExpiredMinutes(timeHelper.parse(timestamp), USER_CACHE_DURATION_M)

    fun getCurrentUserData(): LiveData<AuthUser> = authUserDao.getAuthUser()

    private fun <T> getSuccess(value: T): UserResult<T> = UserResult.Success(value)

    private fun <T> getFailure(error: String): UserResult<T> = UserResult.Failure(error)
}

sealed class UserResult<T> {
    data class Success<T>(val value: T) : UserResult<T>()
    data class Failure<T>(val error: String) : UserResult<T>()
}