package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("search/repositories")
    fun searchRepos(
        @Query("q") query: String,
        @Query("sort") sort: String = "best_match",
        @Query("order") order: String = "desc"
    ): Observable<ApiPage<ApiRepo>>

    @GET("search/users")
    fun searchUsers(
        @Query("q") query: String,
        @Query("sort") sort: String = "best_match",
        @Query("order") order: String = "desc"
    ): Observable<ApiPage<ApiUser>>

    @GET("search/code")
    fun searchCode(
        @Query("q") query: String,
        @Query("sort") sort: String = "best_match",
        @Query("order") order: String = "desc"
    ): Observable<ApiPage<ApiFile>>

    @GET("search/commits")
    fun searchCommits(
        @Query("q") query: String,
        @Query("sort") sort: String = "best_match",
        @Query("order") order: String = "desc"
    ): Observable<ApiPage<ApiCommit>>

    @GET("search/issues")
    fun searchIssues(
        @Query("q") query: String,
        @Query("sort") sort: String = "best_match",
        @Query("order") order: String = "desc"
    ): Observable<ApiPage<ApiIssue>>


}