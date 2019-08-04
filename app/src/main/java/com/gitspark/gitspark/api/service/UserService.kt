package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiAuthUser
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers

interface UserService {

    @GET("user")
    @Headers("Accept: application/json")
    fun getAuthenticatedUser(): Observable<ApiAuthUser>
}