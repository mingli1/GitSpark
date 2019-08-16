package com.gitspark.gitspark.api.service

import com.gitspark.gitspark.api.model.ApiAuthRepoRequest
import com.gitspark.gitspark.api.model.ApiRepo
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET

interface RepoService {

    @GET("user/repos")
    fun getAuthRepos(@Body request: ApiAuthRepoRequest): Observable<List<ApiRepo>>
}