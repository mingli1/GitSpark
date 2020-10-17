package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiAccessToken
import io.reactivex.Single
import retrofit2.http.*

interface LoginService {

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