package com.bilibili.client.core.network

import com.bilibili.client.data.local.SettingsStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Injects Bilibili cookies (SESSDATA, bili_jct) into API requests
 * and captures new cookies from login responses.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val settingsStore: SettingsStore
) : Interceptor {

    private var sessdata: String? = null
    private var biliJct: String? = null

    init {
        // Load stored session on creation
        runBlocking {
            sessdata = settingsStore.getSessdata()
            biliJct = settingsStore.getBiliJct()
        }
    }

    fun setSession(sessdata: String, biliJct: String) {
        this.sessdata = sessdata
        this.biliJct = biliJct
    }

    fun clearSession() {
        sessdata = null
        biliJct = null
        runBlocking { settingsStore.clearAuth() }
    }

    val isLoggedIn: Boolean get() = !sessdata.isNullOrEmpty()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Referer", "https://www.bilibili.com")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")

        // Inject cookies if available
        if (!sessdata.isNullOrEmpty()) {
            requestBuilder.header("Cookie", "SESSDATA=$sessdata; bili_jct=$biliJct")
        }

        val response = chain.proceed(requestBuilder.build())

        // Capture Set-Cookie headers from response (login flow)
        val setCookieHeaders = response.headers("Set-Cookie")
        for (header in setCookieHeaders) {
            when {
                header.startsWith("SESSDATA=") -> {
                    val value = extractCookieValue(header)
                    if (value != null) {
                        sessdata = value
                        runBlocking { settingsStore.saveSession(sessdata = value, biliJct = biliJct ?: "", userId = "") }
                    }
                }
                header.startsWith("bili_jct=") -> {
                    val value = extractCookieValue(header)
                    if (value != null) {
                        biliJct = value
                        if (sessdata != null) {
                            runBlocking { settingsStore.saveSession(sessdata = sessdata!!, biliJct = value, userId = "") }
                        }
                    }
                }
            }
        }

        return response
    }

    private fun extractCookieValue(setCookieHeader: String): String? {
        // Format: "SESSDATA=value; Path=/; Domain=.bilibili.com; HttpOnly"
        val parts = setCookieHeader.split(";")
        val first = parts.firstOrNull() ?: return null
        val eq = first.indexOf('=')
        return if (eq > 0) first.substring(eq + 1) else null
    }
}
