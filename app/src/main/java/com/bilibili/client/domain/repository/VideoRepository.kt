package com.bilibili.client.domain.repository

import com.bilibili.client.domain.model.Video
import com.bilibili.client.domain.model.DanmakuItem
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    suspend fun getHotVideos(page: Int = 1): Result<List<Video>>
    suspend fun getRecommendedVideos(page: Int = 1): Result<List<Video>>
    suspend fun getVideoDetail(bvid: String): Result<Video>
    suspend fun getPlayUrl(bvid: String, cid: Long, quality: Int = 80): Result<PlayUrlResult>
    suspend fun getDanmaku(cid: Long): Result<List<DanmakuItem>>
    suspend fun getRelatedVideos(bvid: String): Result<List<Video>>
    suspend fun getComments(bvid: String, page: Int = 1): Result<CommentResult>
}

data class PlayUrlResult(
    val url: String,
    val dashAudio: String?,
    val dashVideo: String?,
    val acceptQuality: List<QualityItem>
)

data class QualityItem(
    val quality: Int,
    val description: String
)

data class CommentResult(
    val comments: List<Comment>,
    val total: Int,
    val hasMore: Boolean
)

data class Comment(
    val id: Long,
    val content: String,
    val userName: String,
    val userAvatar: String,
    val likes: Int,
    val replies: Int,
    val pubdate: Long
)
