package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiCombinedRepoStatus
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ChecksService {

    @GET("repos/{owner}/{repo}/commits/{ref}/status")
    fun getCombinedStatus(
        @Path("owner") username: String,
        @Path("repo") repoName: String,
        @Path("ref") ref: String
    ): Single<ApiCombinedRepoStatus>
}