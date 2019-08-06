package com.gitspark.gitspark.helper

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

private const val BASE_URL = "https://api.github.com"
private const val TOKEN = "token"

class RetrofitHelperTest {

    private lateinit var retrofitHelper: RetrofitHelper

    @Before
    fun setup() {
        retrofitHelper = RetrofitHelper()
    }

    @Test
    fun shouldSetTokenAndCacheRetrofit() {
        val key = "$BASE_URL$TOKEN"

        val retrofit = retrofitHelper.getRetrofit(baseUrl = BASE_URL, token = TOKEN)

        assertThat(retrofitHelper.token).isEqualTo(TOKEN)
        assertThat(retrofitHelper.retrofitCache).containsKey(key)
        assertThat(retrofit).isEqualTo(retrofitHelper.retrofitCache[key])
    }
}