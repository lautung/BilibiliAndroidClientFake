package com.bilibili.client.data.repository

import com.bilibili.client.core.network.BiliApi
import com.bilibili.client.domain.model.LiveRoom
import com.bilibili.client.domain.repository.LivePlayResult
import com.bilibili.client.domain.repository.LiveRepository
import javax.inject.Inject

class LiveRepositoryImpl @Inject constructor(
    private val api: BiliApi
) : LiveRepository {

    override suspend fun getLiveRooms(page: Int): Result<List<LiveRoom>> = runCatching {
        val response = api.getLiveRooms(page = page).body() ?: throw Exception("Empty response")
        if (response.code != 0) throw Exception(response.message)
        response.data?.list?.map { room ->
            LiveRoom(
                roomId = room.roomId,
                uid = room.uid,
                title = room.title,
                uploader = room.uname,
                uploaderAvatar = room.face,
                viewerCount = room.online,
                coverUrl = room.userCover,
                key = room.key,
                playUrl = room.playUrl,
                isLive = room.liveStatus == 1
            )
        } ?: emptyList()
    }

    override suspend fun getLivePlayUrl(roomId: Long): Result<LivePlayResult> = runCatching {
        val response = api.getLivePlayUrl(roomId = roomId).body() ?: throw Exception("Empty response")
        if (response.code != 0) throw Exception(response.message)
        val data = response.data ?: throw Exception("Live play URL not available")

        val url = data.durl?.firstOrNull()?.url ?: ""
        val host = data.durl?.firstOrNull()?.host ?: ""

        LivePlayResult(
            url = url,
            host = host,
            protocol = data.protocol ?: "http"
        )
    }

    override suspend fun getFollowedRooms(): Result<List<LiveRoom>> = runCatching {
        emptyList()
    }
}
