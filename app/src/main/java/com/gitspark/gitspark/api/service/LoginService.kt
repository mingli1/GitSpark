package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiAccessToken
import com.gitspark.gitspark.api.model.ApiToken
import com.gitspark.gitspark.api.model.ApiAuthRequest
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*

interface LoginService {

    @PUT("authorizations/clients/{clientId}")
    @Headers("Accept: application/json")
    fun putAuthorizations(
        @Path("clientId") clientId: String,
        @Body request: ApiAuthRequest
    ): Observable<ApiToken>

    @DELETE("authorizations/{authId}")
    fun deleteAuthorization(@Path("authId") authId: Long): Completable

    @FormUrlEncoded
    @POST("access_token")
    @Headers("Accept: application/json")
    fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("state") state: String,
        @Field("redirect_uri") redirectUrl: String
    ): Single<ApiAccessToken>
}