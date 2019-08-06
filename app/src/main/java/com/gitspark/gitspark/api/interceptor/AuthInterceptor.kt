package com.gitspark.gitspark.api.interceptor

import androidx.annotation.VisibleForTesting
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(private val token: String?) : Interceptor {

    override fun intercept(chain: Chain): Response? {
        return chain.proceed(getNewRequest(chain.request()))
    }

    @VisibleForTesting
    fun getNewRequest(original: Request): Request {
        var request = original
        token?.let {
            val authToken = if (it.startsWith("Basic")) it else "token $it"
            request = request.newBuilder()
                .addHeader("Authorization", authToken)
                .build()
        }
        return request
    }
}