package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiIssue
import com.gitspark.gitspark.api.model.ApiIssueComment
import com.gitspark.gitspark.api.model.ApiIssueEvent
import com.gitspark.gitspark.api.model.ApiPage
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val ISSUE_EVENTS_PER_PAGE = 50

interface IssueService {

    @GET("repos/{owner}/{repo}/issues/{issue_number}")
    fun getIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNum: Int
    ): Observable<ApiIssue>

    @GET("repos/{owner}/{repo}/issues/{issue_number}/comments")
    fun getIssueComments(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNum: Int,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = ISSUE_EVENTS_PER_PAGE
    ): Observable<ApiPage<ApiIssueComment>>

    @GET("repos/{owner}/{repo}/issues/{issue_number}/events")
    fun getIssueEvents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNum: Int,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = ISSUE_EVENTS_PER_PAGE
    ): Observable<ApiPage<ApiIssueEvent>>
}