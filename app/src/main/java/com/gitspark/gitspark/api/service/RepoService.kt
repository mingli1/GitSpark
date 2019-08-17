package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiRepo
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RepoService {

    @GET("user/repos")
    @Headers("Accept: application/json", "Accept: application/vnd.github.mercy-preview+json")
    fun getAuthRepos(
        @Query("visibility") visibility: String,
        @Query("affiliation") affiliation: String,
        @Query("sort") sort: String
    ): Observable<List<ApiRepo>>
}