package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiAuthUser
import com.gitspark.gitspark.api.model.ApiPage
import com.gitspark.gitspark.api.model.ApiUser
import io.reactivex.Observable
import retrofit2.http.*

private const val PER_PAGE = 25

interface UserService {

    @GET("user")
    @Headers("Accept: application/json")
    fun getAuthenticatedUser(): Observable<ApiAuthUser>

    @GET
    fun getContributionsSvg(@Url url: String): Observable<String>

    @GET("users/{username}/followers")
    @Headers("Accept: application/json")
    fun getUserFollowers(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = PER_PAGE
    ): Observable<ApiPage<ApiUser>>
}