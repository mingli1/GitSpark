package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

const val USER_PER_PAGE = 50

interface UserService {

    @GET("user")
    @Headers("Accept: application/json")
    fun getAuthenticatedUser(): Observable<ApiAuthUser>

    @GET("users/{username}")
    @Headers("Accept: application/json")
    fun getUser(@Path("username") username: String): Observable<ApiUser>

    @GET
    fun getContributionsSvg(@Url url: String): Observable<String>

    @GET("user/followers")
    fun getAuthUserFollowers(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = USER_PER_PAGE
    ): Observable<ApiPage<ApiUser>>

    @GET("user/following")
    fun getAuthUserFollowing(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = USER_PER_PAGE
    ): Observable<ApiPage<ApiUser>>

    @GET("users/{username}/followers")
    @Headers("Accept: application/json")
    fun getUserFollowers(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = USER_PER_PAGE
    ): Observable<ApiPage<ApiUser>>

    @GET("users/{username}/following")
    @Headers("Accept: application/json")
    fun getUserFollowing(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = USER_PER_PAGE
    ): Observable<ApiPage<ApiUser>>

    @GET("user/following/{username}")
    fun isFollowing(@Path("username") username: String): Completable

    @PUT("user/following/{username}")
    @Headers("Content-Length: 0")
    fun followUser(@Path("username") username: String): Completable

    @DELETE("user/following/{username}")
    fun unfollowUser(@Path("username") username: String): Completable

    @PATCH("user")
    fun updateUser(@Body request: ApiEditProfileRequest): Observable<ApiAuthUser>

    @GET("rate_limit")
    fun getRateLimit(): Observable<ApiRateLimit>
}