package com.bilibili.client.domain.repository

import com.bilibili.client.domain.model.User

interface AuthRepository {
    suspend fun getQrCode(): Result<QrCodeResult>
    suspend fun pollQrStatus(qrcodeKey: String): Result<QrStatus>
    suspend fun getCurrentUser(): Result<User>
    suspend fun logout(): Result<Unit>
    fun isLoggedIn(): Boolean
}

data class QrCodeResult(
    val url: String,
    val qrcodeKey: String
)

enum class QrStatus {
    SCANNED,
    CONFIRMED,
    EXPIRED,
    WAITING
}

data class SearchResult(
    val videos: List<SearchVideoItem>,
    val users: List<SearchUserItem>,
    val totalResults: Int
)

data class SearchVideoItem(
    val bvid: String,
    val title: String,
    val coverUrl: String,
    val uploader: String,
    val views: Long,
    val duration: String
)

data class SearchUserItem(
    val mid: Long,
    val name: String,
    val avatar: String,
    val followerCount: Long,
    val videoCount: Long
)
