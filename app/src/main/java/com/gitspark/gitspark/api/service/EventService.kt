package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiEvent
import com.gitspark.gitspark.api.model.ApiPage
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EventService {

    @GET("events")
    fun getPublicEvents(@Query("page") page: Int): Observable<ApiPage<ApiEvent>>

    @GET("users/{username}/events")
    fun getEvents(
        @Path("username") username: String,
        @Query("page") page: Int
    ): Observable<ApiPage<ApiEvent>>

    @GET("users/{username}/received_events")
    fun getReceivedEvents(
        @Path("username") username: String,
        @Query("page") page: Int
    ): Observable<ApiPage<ApiEvent>>

    @GET("repos/{owner}/{repo}/events")
    fun getRepoEvents(
        @Path("owner") username: String,
        @Path("repo") repoName: String,
        @Query("page") page: Int
    ): Observable<ApiPage<ApiEvent>>
}