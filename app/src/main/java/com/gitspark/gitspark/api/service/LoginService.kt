package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiToken
import com.gitspark.gitspark.api.model.ApiAuthRequest
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface LoginService {

    @POST("authorizations")
    @Headers("Accept: application/json")
    fun postAuthorizations(@Body auth: ApiAuthRequest): Observable<ApiToken>

    @GET("authorizations")
    @Headers("Accept: application/json")
    fun getAuthorizations(): Observable<List<ApiToken>>

    @DELETE("authorizations/{authId}")
    fun deleteAuthorization(@Path("authId") authId: Int): Observable<ResponseBody>
}