package br.com.vibetube.app.core.network

import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder()
            .header("User-Agent", "VibeTubeApp/0.1 (Android; Kotlin)")
            .header("Accept", "application/json, text/html;q=0.9")
            .build()
        return chain.proceed(req)
    }
}
