package com.bilibili.client.data.repository

import com.bilibili.client.core.network.BiliApi
import com.bilibili.client.data.model.toDomainVideo
import com.bilibili.client.domain.model.DanmakuItem
import com.bilibili.client.domain.model.DanmakuType
import com.bilibili.client.domain.model.Video
import com.bilibili.client.domain.repository.Comment
import com.bilibili.client.domain.repository.CommentResult
import com.bilibili.client.domain.repository.PlayUrlResult
import com.bilibili.client.domain.repository.QualityItem
import com.bilibili.client.domain.repository.VideoRepository
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val api: BiliApi
) : VideoRepository {

    override suspend fun getHotVideos(page: Int): Result<List<Video>> = runCatching {
        val response = api.getPopular(page = page).body() ?: throw Exception("Empty response")
        if (response.code != 0) throw Exception(response.message)
        response.data?.list?.map { it.toDomainVideo() } ?: emptyList()
    }

    override suspend fun getRecommendedVideos(page: Int): Result<List<Video>> = runCatching {
        val response = api.getRecommendedVideos().body() ?: throw Exception("Empty response")
        if (response.code != 0) throw Exception(response.message)
        response.data?.list?.map { it.toDomainVideo() } ?: emptyList()
    }

    override suspend fun getVideoDetail(bvid: String): Result<Video> = runCatching {
        val response = api.getVideoDetail(bvid = bvid).body() ?: throw Exception("Empty response")
        if (response.code != 0) throw Exception(response.message)
        val data = response.data ?: throw Exception("Video not found")
        data.toDomainVideo()
    }

    override suspend fun getPlayUrl(bvid: String, cid: Long, quality: Int): Result<PlayUrlResult> = runCatching {
        val response = api.getPlayUrl(bvid = bvid, cid = cid, qn = quality).body() ?: throw Exception("Empty response")
        if (response.code != 0) throw Exception(response.message)
        val data = response.data ?: throw Exception("Play URL not available")

        val dash = data.dash
        val dashVideo = dash?.video?.maxByOrNull { it.bandwidth }
        val dashAudio = dash?.audio?.maxByOrNull { it.bandwidth }

        PlayUrlResult(
            url = data.durl?.firstOrNull()?.url ?: dashVideo?.baseUrl ?: "",
            dashAudio = dashAudio?.baseUrl,
            dashVideo = dashVideo?.baseUrl,
            acceptQuality = (data.acceptQuality ?: emptyList()).mapIndexed { index, quality ->
                QualityItem(
                    quality = quality,
                    description = data.acceptDescription?.getOrElse(index) { "" } ?: ""
                )
            }
        )
    }

    override suspend fun getDanmaku(cid: Long): Result<List<DanmakuItem>> = runCatching {
        val responseBody = api.getDanmaku(oid = cid, segment = 1)
        val xml = responseBody.string()
        parseDanmakuXml(xml)
    }

    override suspend fun getRelatedVideos(bvid: String): Result<List<Video>> = runCatching {
        val response = api.getVideoDetail(bvid = bvid).body() ?: throw Exception("Empty response")
        response.data?.related?.map { it.toDomainVideo() } ?: emptyList()
    }

    override suspend fun getComments(bvid: String, page: Int): Result<CommentResult> = runCatching {
        // TODO: Extract aid from bvid via an API call or mapping
        // For now return empty since we need aid for the comments endpoint
        CommentResult(comments = emptyList(), total = 0, hasMore = false)
    }

    private fun parseDanmakuXml(xml: String): List<DanmakuItem> {
        val danmakuList = mutableListOf<DanmakuItem>()
        // Standard Bilibili danmaku XML format
        val regex = Regex("<d p=\"([^\"]+)\"[^>]*>([^<]*)</d>")
        var id = 0L
        for (match in regex.findAll(xml)) {
            val params = match.groupValues[1].split(",")
            val text = match.groupValues[2]
            if (text.isBlank()) continue
            val timestampMs = (params.getOrNull(0)?.toFloatOrNull()?.times(1000) ?: 0f).toLong()
            val type = when (params.getOrNull(1)?.toIntOrNull()) {
                1 -> DanmakuType.ROLLING
                4 -> DanmakuType.BOTTOM
                5 -> DanmakuType.TOP
                else -> DanmakuType.ROLLING
            }
            val color = (params.getOrNull(3)?.toLongOrNull()?.toInt() ?: 0xFFFFFF) or (0xFF000000).toInt()
            val fontSize = params.getOrNull(2)?.toFloatOrNull() ?: 25f

            danmakuList.add(
                DanmakuItem(
                    id = id++,
                    text = text,
                    timestampMs = timestampMs,
                    type = type,
                    color = color,
                    fontSize = if (fontSize <= 0) 25f else fontSize
                )
            )
        }
        return danmakuList
    }
}
