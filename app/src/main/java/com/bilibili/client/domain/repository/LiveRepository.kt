package com.bilibili.client.domain.repository

import com.bilibili.client.domain.model.LiveRoom

interface LiveRepository {
    suspend fun getLiveRooms(page: Int = 1): Result<List<LiveRoom>>
    suspend fun getLivePlayUrl(roomId: Long): Result<LivePlayResult>
    suspend fun getFollowedRooms(): Result<List<LiveRoom>>
}

data class LivePlayResult(
    val url: String,
    val host: String,
    val protocol: String
)
