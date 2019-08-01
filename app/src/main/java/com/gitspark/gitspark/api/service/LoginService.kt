package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiToken
import com.gitspark.gitspark.api.model.ApiAuthRequest
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginService {

    @POST("authorizations")
    @Headers("Accept: application/json")
    fun postAuthorizations(@Body auth: ApiAuthRequest): Observable<ApiToken>
}