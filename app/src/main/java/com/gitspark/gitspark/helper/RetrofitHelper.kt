package com.gitspark.gitspark.helper

import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.api.interceptor.GithubInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitHelper @Inject constructor() {

    private val retrofitCache = mutableMapOf<String, Retrofit>()
    private var token: String? = null

    fun getRetrofit(baseUrl: String = BuildConfig.GITHUB_URL, token: String? = null): Retrofit {
        this.token = token
        if (!retrofitCache.containsKey(baseUrl)) addRetrofit(baseUrl)
        return checkNotNull(retrofitCache[baseUrl]) { "Failed to retrieve Retrofit from cache." }
    }

    private fun addRetrofit(baseUrl: String) {
        retrofitCache[baseUrl] =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
    }

    private fun getOkHttpClient() =
            OkHttpClient.Builder()
                .addInterceptor(GithubInterceptor(token))
                .build()
}