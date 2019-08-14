package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiAuthUser
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface UserService {

    @GET("user")
    @Headers("Accept: application/json")
    fun getAuthenticatedUser(): Observable<ApiAuthUser>

    @GET
    fun getContributionsSvg(@Url url: String): Observable<String>
}