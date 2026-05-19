package com.bilibili.client.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.security.MessageDigest

/**
 * Bilibili WBI sign interceptor.
 * Adds w_rid and wts parameters to API requests that require signing.
 *
 * The signing process:
 * 1. Fetch img_key + sub_key from Bilibili nav API
 * 2. Mix keys: mix = sub_key.substring(0, 4) + img_key.substring(0, 4)
 * 3. Sort query params, append mix, MD5 hash -> w_rid
 * 4. Append w_rid + wts (timestamp) to URL
 */
class WbiSignInterceptor : Interceptor {

    private var imgKey: String = ""
    private var subKey: String = ""
    private var lastFetch: Long = 0

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url

        // Only sign bilibili.com API requests (excluding auth endpoints)
        if (url.host.contains("bilibili.com") && !url.encodedPath.contains("qrcode")) {
            if (isStale()) {
                // TODO: Fetch keys from Bilibili nav API
                // imgKey, subKey = fetchKeys()
            }
            if (imgKey.isNotEmpty() && subKey.isNotEmpty()) {
                val signedUrl = signUrl(url.toString())
                val newRequest = originalRequest.newBuilder()
                    .url(signedUrl)
                    .build()
                return chain.proceed(newRequest)
            }
        }

        return chain.proceed(originalRequest)
    }

    private fun signUrl(url: String): String {
        // mix = subKey.substring(0, 4) + imgKey.substring(0, 4)
        // Sort query params alphabetically
        // Append mixKey to params
        // MD5 hash -> w_rid
        // Append w_rid + wts to URL
        return url // TODO: Full implementation
    }

    private fun isStale(): Boolean {
        return System.currentTimeMillis() - lastFetch > 4 * 60 * 60 * 1000L // 4 hours
    }

    fun fetchKeys() {
        // TODO: GET https://api.bilibili.com/x/web-interface/nav
        // Extract img_key and sub_key from response
        // Update lastFetch
    }

    companion object {
        fun md5(input: String): String {
            val digest = MessageDigest.getInstance("MD5")
            return digest.digest(input.toByteArray())
                .joinToString("") { "%02x".format(it) }
        }

        private val MIXIN_KEY_TABLE = listOf(
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35,
            27, 43, 5, 49, 33, 9, 42, 19, 29, 28, 14, 37, 12, 52, 56, 55,
            16, 17, 26, 7, 57, 13, 44, 48, 11, 1, 25, 39, 51, 24, 38, 40,
            61, 36, 20, 6, 4, 22, 0, 21, 30, 54, 59, 60, 34, 41
        )

        fun getMixinKey(key: String): String {
            return MIXIN_KEY_TABLE.take(key.length).map { key[it] }.joinToString("")
        }
    }
}
