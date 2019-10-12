package com.gitspark.gitspark.helper

import androidx.annotation.VisibleForTesting
import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.api.interceptor.AuthInterceptor
import com.gitspark.gitspark.api.interceptor.PageInterceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val TIMEOUT_TIME_M = 3L

@Singleton
class RetrofitHelper @Inject constructor() {

    @VisibleForTesting val retrofitCache = mutableMapOf<String, Retrofit>()
    @VisibleForTesting var token: String? = null

    fun getRetrofit(
        baseUrl: String = BuildConfig.GITHUB_URL,
        token: String? = null,
        lenient: Boolean = false
    ): Retrofit {
        this.token = token
        val key = "$baseUrl${token ?: ""}"
        if (!retrofitCache.containsKey(key)) {
            if (lenient) addLenientRetrofit(key, baseUrl) else addRetrofit(key, baseUrl)
        }
        return checkNotNull(retrofitCache[key]) { "Failed to retrieve Retrofit from cache." }
    }

    private fun addRetrofit(key: String, baseUrl: String) {
        retrofitCache[key] =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
    }

    private fun addLenientRetrofit(key: String, baseUrl: String) {
        retrofitCache[key] =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpClient())
                .addConverterFactory(StringConverterFactory())
                .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        val okHttp = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_TIME_M, TimeUnit.MINUTES)
            .writeTimeout(TIMEOUT_TIME_M, TimeUnit.MINUTES)
            .readTimeout(TIMEOUT_TIME_M, TimeUnit.MINUTES)
            .addInterceptor(AuthInterceptor(token))
            .addInterceptor(PageInterceptor())

        if (BuildConfig.DEBUG)
            okHttp.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })

        return okHttp.build()
    }

    class StringConverterFactory : Converter.Factory() {

        override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *>? {
            return StringConverter()
        }

        class StringConverter : Converter<ResponseBody, String> {
            override fun convert(value: ResponseBody): String? = value.string()
        }
    }
}