package com.bilibili.client.data.repository

import com.bilibili.client.core.network.BiliApi
import com.bilibili.client.data.local.SettingsStore
import com.bilibili.client.domain.model.User
import com.bilibili.client.domain.repository.AuthRepository
import com.bilibili.client.domain.repository.QrCodeResult
import com.bilibili.client.domain.repository.QrStatus
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: BiliApi,
    private val settingsStore: SettingsStore
) : AuthRepository {

    override suspend fun getQrCode(): Result<QrCodeResult> = runCatching {
        val response = api.getLoginQrCode().body() ?: throw Exception("Empty response")
        if (response.code != 0) throw Exception(response.message)
        val data = response.data ?: throw Exception("QR code data not available")

        QrCodeResult(
            url = data.url,
            qrcodeKey = data.qrcodeKey
        )
    }

    override suspend fun pollQrStatus(qrcodeKey: String): Result<QrStatus> = runCatching {
        val response = api.pollQrLogin(qrcodeKey = qrcodeKey).body() ?: throw Exception("Empty response")
        when (response.code) {
            0 -> QrStatus.CONFIRMED
            86038 -> QrStatus.EXPIRED
            86090 -> QrStatus.SCANNED
            else -> QrStatus.WAITING
        }
    }

    override suspend fun getCurrentUser(): Result<User> = runCatching {
        val response = api.getCurrentUser().body() ?: throw Exception("Empty response")
        if (response.code != 0) throw Exception(response.message)
        val data = response.data ?: throw Exception("User data not available")
        User(
            mid = data.mid.toLongOrNull() ?: 0,
            name = data.name,
            avatar = data.face,
            sign = data.sign,
            level = data.levelInfo?.currentLevel ?: 0,
            followerCount = 0,
            followingCount = 0,
            videoCount = 0
        )
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        api.logout()
    }

    override fun isLoggedIn(): Boolean {
        return runCatching { kotlinx.coroutines.runBlocking { settingsStore.hasSession() } }.getOrDefault(false)
    }
}
