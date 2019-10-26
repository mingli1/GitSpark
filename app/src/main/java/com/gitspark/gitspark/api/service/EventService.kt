package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiEvent
import com.gitspark.gitspark.api.model.ApiPage
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EventService {

    @GET("users/{username}/events")
    fun getEvents(
        @Path("username") username: String,
        @Query("page") page: Int
    ): Observable<ApiPage<ApiEvent>>
}