package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

const val ISSUE_EVENTS_PER_PAGE = 50
const val ASSIGNEES_PER_PAGE = 100

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

    @PUT("repos/{owner}/{repo}/issues/{issue_number}/lock")
    fun lockIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNum: Int,
        @Query("lock_reason") reason: String
    ): Completable

    @DELETE("repos/{owner}/{repo}/issues/{issue_number}/lock")
    fun unlockIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNum: Int
    ): Completable

    @PATCH("repos/{owner}/{repo}/issues/{issue_number}")
    fun editIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNum: Int,
        @Body request: ApiIssueEditRequest
    ): Observable<ApiIssue>

    @POST("repos/{owner}/{repo}/issues/{issue_number}/comments")
    fun createComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNum: Int,
        @Body request: ApiIssueCommentRequest
    ): Observable<ApiIssueComment>

    @PATCH("repos/{owner}/{repo}/issues/comments/{comment_id}")
    fun editComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("comment_id") commentId: Long,
        @Body body: ApiIssueCommentRequest
    ): Completable

    @DELETE("repos/{owner}/{repo}/issues/comments/{comment_id}")
    fun deleteComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("comment_id") commentId: Long
    ): Completable

    @GET("repos/{owner}/{repo}/assignees")
    fun getAvailableAssignees(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = ASSIGNEES_PER_PAGE
    ): Observable<ApiPage<ApiUser>>
}