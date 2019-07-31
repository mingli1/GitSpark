package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiToken
import com.gitspark.gitspark.models.Authorization
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginService {

    @POST("authorizations")
    @Headers("Accept: application/json")
    fun postAuthorizations(@Body auth: Authorization): Observable<ApiToken>
}