package app.rootstock.data.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class JsonInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder: Request.Builder = chain.request().newBuilder()
        requestBuilder.header("Content-Type", "application/json")
        return chain.proceed(requestBuilder.build())
    }
}