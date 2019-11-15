package com.gitspark.gitspark.api.interceptor

import android.net.Uri
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import okhttp3.ResponseBody

private const val DELIM_LINKS = ","
private const val DELIM_LINK_PARAM = ";"
private const val PAGE_QUERY = "page"

class PageInterceptor : Interceptor {

    override fun intercept(chain: Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.isSuccessful) {
            if (response.peekBody(1).string() == "[") {
                var attrs = ""
                response.header("link")?.let { attrs = getAttrs(it) }
                response.body()?.let { body ->
                    return response.newBuilder().body(
                        ResponseBody.create(
                            body.contentType(),
                            "{$attrs\"items\":${body.string()}}"
                        )
                    ).build()
                }
            }
            else {
                response.header("link")?.let {
                    val attrs = getAttrs(it)
                    if (attrs.isNotEmpty()) {
                        response.body()?.let { body ->
                            val bodyStr = body.string().substring(1)
                            return response.newBuilder().body(
                                ResponseBody.create(
                                    body.contentType(),
                                    "{$attrs$bodyStr"
                                )
                            ).build()
                        }
                    }
                }
            }
        }

        return response
    }

    private fun getAttrs(header: String): String {
        var attrs = ""
        header.split(DELIM_LINKS).forEach { link ->
            val segments = link.split(DELIM_LINK_PARAM)
            val url = segments[0].trim()
            val page = Uri.parse(url.substring(1, url.length - 1)).getQueryParameter(PAGE_QUERY)
            val relStr = segments[1].trim()
            val rel = relStr.substring(5, relStr.length - 1)
            page?.let { p ->
                attrs += String.format("\"%s\":%s,", rel, p)
            }
        }
        return attrs
    }
}