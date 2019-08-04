package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiToken
import com.gitspark.gitspark.api.model.ApiAuthRequest
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface LoginService {

    @PUT("authorizations/clients/{clientId}")
    @Headers("Accept: application/json")
    fun putAuthorizations(
        @Path("clientId") clientId: String,
        @Body request: ApiAuthRequest
    ): Observable<ApiToken>
}