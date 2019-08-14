package com.gitspark.gitspark.helper

import androidx.annotation.VisibleForTesting
import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.api.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

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

    private fun getOkHttpClient() =
            OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(token))
                .build()

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