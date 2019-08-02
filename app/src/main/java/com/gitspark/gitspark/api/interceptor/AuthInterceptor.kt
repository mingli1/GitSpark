package com.gitspark.gitspark.api.interceptor

import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

class AuthInterceptor(private val token: String?) : Interceptor {

    override fun intercept(chain: Chain): Response? {
        var request = chain.request()

        token?.let {
            val authToken = if (it.startsWith("Basic")) it else "token $it"
            request = request.newBuilder()
                .addHeader("Authorization", authToken)
                .build()
        }

        return chain.proceed(request)
    }
}