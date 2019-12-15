package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiIssue
import com.gitspark.gitspark.api.model.ApiPage
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

const val ISSUES_PER_PAGE = 30

interface IssueService {

    @GET("issues")
    fun getIssues(
        @Query("filter") filter: String,
        @Query("state") state: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = ISSUES_PER_PAGE
    ): Observable<ApiPage<ApiIssue>>
}