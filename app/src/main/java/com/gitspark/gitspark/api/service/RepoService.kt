package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiRepo
import com.gitspark.gitspark.api.model.ApiStarredRepo
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RepoService {

    @GET("user/repos")
    @Headers("Accept: application/vnd.github.mercy-preview+json")
    fun getAuthRepos(
        @Query("visibility") visibility: String,
        @Query("affiliation") affiliation: String,
        @Query("sort") sort: String
    ): Observable<List<ApiRepo>>

    @GET("user/starred")
    @Headers("Accept: application/vnd.github.v3.star+json")
    fun getAuthStarredRepos(): Observable<List<ApiStarredRepo>>
}