package com.gitspark.gitspark.api.interceptor

import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

class GithubInterceptor(private val token: String?) : Interceptor {

    override fun intercept(chain: Chain): Response? {
        var request = chain.request()

        token?.let {
            request = request.newBuilder()
                .addHeader("Authorization", it)
                .build()
        }

        return chain.proceed(request)
    }
}