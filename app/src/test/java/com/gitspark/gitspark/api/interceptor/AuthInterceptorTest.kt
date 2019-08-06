package com.gitspark.gitspark.api.interceptor

import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

private const val BASIC_TOKEN = "Basic 123"
private const val OAUTH_TOKEN = "abc123"

class AuthInterceptorTest {

    private lateinit var authInterceptor: AuthInterceptor
    private lateinit var request: Request

    @Before
    fun setup() {
        request = Request.Builder()
            .url("https://api.github.com")
            .build()
    }

    @Test
    fun shouldAddBasicAuthHeader() {
        authInterceptor = AuthInterceptor(BASIC_TOKEN)
        val newRequest = authInterceptor.getNewRequest(request)
        assertThat(newRequest.header("Authorization")).isEqualTo(BASIC_TOKEN)
    }

    @Test
    fun shouldAddOAuthHeader() {
        authInterceptor = AuthInterceptor(OAUTH_TOKEN)
        val newRequest = authInterceptor.getNewRequest(request)
        assertThat(newRequest.header("Authorization")).isEqualTo("token $OAUTH_TOKEN")
    }

    @Test
    fun shouldNotAddHeaderOnNullToken() {
        authInterceptor = AuthInterceptor(null)
        val newRequest = authInterceptor.getNewRequest(request)
        assertThat(newRequest).isEqualTo(request)
    }
}