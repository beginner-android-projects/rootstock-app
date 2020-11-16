package app.rootstock.data.network

import android.content.Context
import android.util.Log
import okhttp3.*
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class CacheInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        val cacheControl = CacheControl.Builder()
            .maxAge(5, TimeUnit.MINUTES)
            .build()

        return response.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .header("Cache-Control", cacheControl.toString())
            .build()
    }
}


class CacheCleaner @Inject constructor(private val okHttpClient: OkHttpClient) {

    fun cleanCache() {
        okHttpClient.cache()?.evictAll()
    }
}