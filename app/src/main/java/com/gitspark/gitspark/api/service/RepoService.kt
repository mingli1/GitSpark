package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

const val REPO_PER_PAGE = 50
const val BRANCHES_PER_PAGE = 50

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

    @GET
    @Headers("Accept: application/vnd.github.VERSION.raw")
    fun getRawContent(@Url url: String): Observable<String>

    @GET("repos/{owner}/{repo}/readme")
    fun getReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<ApiRepoContent>

    @GET("repos/{owner}/{repo}/contents/{path}")
    fun getFile(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String = "",
        @Query("ref") ref: String
    ): Observable<ApiRepoContent>

    @GET("repos/{owner}/{repo}/contents/{path}")
    fun getDirectory(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String = "",
        @Query("ref") ref: String
    ): Observable<ApiPage<ApiRepoContent>>

    @GET("repos/{owner}/{repo}/branches")
    fun getBranches(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = BRANCHES_PER_PAGE
    ): Observable<ApiPage<ApiBranch>>

    @GET("user/starred/{owner}/{repo}")
    fun isStarredByAuthUser(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Completable

    @GET("repos/{owner}/{repo}/subscription")
    fun isWatchedByAuthUser(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<ApiSubscribed>

    @PUT("repos/{owner}/{repo}/subscription")
    fun watchRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("subscribed") subscribed: Boolean,
        @Query("ignored") ignored: Boolean
    ): Completable

    @DELETE("repos/{owner}/{repo}/subscription")
    fun unwatchRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Completable

    @PUT("user/starred/{owner}/{repo}")
    fun starRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Completable

    @DELETE("user/starred/{owner}/{repo}")
    fun unstarRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Completable

    @POST("repos/{owner}/{repo}/forks")
    fun forkRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Completable

    @GET("repos/{owner}/{repo}/subscribers")
    fun getWatchers(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<ApiPage<ApiUser>>
}