package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiPage
import com.gitspark.gitspark.api.model.ApiRepo
import com.gitspark.gitspark.api.model.ApiRepoContent
import com.gitspark.gitspark.api.model.ApiStarredRepo
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

const val REPO_PER_PAGE = 50

interface RepoService {

    @GET("user/repos")
    @Headers("Accept: application/vnd.github.mercy-preview+json")
    fun getAuthRepos(
        @Query("visibility") visibility: String,
        @Query("affiliation") affiliation: String,
        @Query("sort") sort: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = REPO_PER_PAGE
    ): Observable<ApiPage<ApiRepo>>

    @GET("user/starred")
    @Headers("Accept: application/vnd.github.v3.star+json")
    fun getAuthStarredRepos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = REPO_PER_PAGE
    ): Observable<ApiPage<ApiStarredRepo>>

    @GET("users/{username}/repos")
    @Headers("Accept: application/vnd.github.mercy-preview+json")
    fun getRepos(
        @Path("username") username: String,
        @Query("visibility") visibility: String,
        @Query("affiliation") affiliation: String,
        @Query("sort") sort: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = REPO_PER_PAGE
    ): Observable<ApiPage<ApiRepo>>

    @GET("users/{username}/starred")
    @Headers("Accept: application/vnd.github.v3.star+json")
    fun getStarredRepos(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = REPO_PER_PAGE
    ): Observable<ApiPage<ApiStarredRepo>>

    @GET("repos/{owner}/{repo}/readme")
    fun getReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<ApiRepoContent>
}