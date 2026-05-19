package com.bilibili.client.core.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that injects Bilibili cookies (SESSDATA) into API requests.
 * Cookies are stored securely via EncryptedSharedPreferences.
 */
class AuthInterceptor : Interceptor {

    private var sessdata: String? = null
    private var biliJct: String? = null

    fun setSession(sessdata: String, biliJct: String) {
        this.sessdata = sessdata
        this.biliJct = biliJct
    }

    fun clearSession() {
        sessdata = null
        biliJct = null
    }

    val isLoggedIn: Boolean get() = sessdata != null

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (sessdata == null) return chain.proceed(originalRequest)

        val request = originalRequest.newBuilder()
            .header("Cookie", "SESSDATA=$sessdata; bili_jct=$biliJct")
            .header("Referer", "https://www.bilibili.com")
            .header("User-Agent", "Mozilla/5.0 BiliClient/1.0")
            .build()

        return chain.proceed(request)
    }
}
