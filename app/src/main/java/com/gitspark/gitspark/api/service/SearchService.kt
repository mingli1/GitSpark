package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SearchService {

    @GET("search/repositories")
    fun searchRepos(
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Int,
        @Query("sort") sort: String = "best_match",
        @Query("order") order: String = "desc"
    ): Observable<ApiPage<ApiRepo>>

    @GET("search/users")
    fun searchUsers(
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Int,
        @Query("sort") sort: String = "best_match",
        @Query("order") order: String = "desc"
    ): Observable<ApiPage<ApiUser>>

    @GET("search/code")
    fun searchCode(
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Int,
        @Query("sort") sort: String = "best_match",
        @Query("order") order: String = "desc"
    ): Observable<ApiPage<ApiFile>>

    @GET("search/commits")
    @Headers("Accept: application/vnd.github.cloak-preview")
    fun searchCommits(
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Int,
        @Query("sort") sort: String = "best_match",
        @Query("order") order: String = "desc"
    ): Observable<ApiPage<ApiCommit>>

    @GET("search/issues")
    fun searchIssues(
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Int,
        @Query("sort") sort: String = "best_match",
        @Query("order") order: String = "desc"
    ): Observable<ApiPage<ApiIssue>>
}