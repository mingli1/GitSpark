package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiEvent
import com.gitspark.gitspark.api.model.ApiPage
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface EventService {

    @GET("users/{username}/events")
    fun getEvents(@Path("username") username: String): Observable<ApiPage<ApiEvent>>
}