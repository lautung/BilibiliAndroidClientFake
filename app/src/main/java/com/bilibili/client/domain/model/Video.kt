package com.bilibili.client.domain.model

data class Video(
    val bvid: String,
    val aid: Long,
    val title: String,
    val description: String,
    val uploader: String,
    val uploaderAvatar: String,
    val uploaderMid: Long,
    val views: Long,
    val danmakuCount: Long,
    val likes: Long,
    val coins: Long,
    val favorites: Long,
    val duration: String,
    val coverUrl: String,
    val picUrl: String,
    val pubdate: Long,
    val tname: String,
    val quality: Int,
    val acceptQuality: List<String>,
    val dashVideoUrl: String? = null,
    val dashAudioUrl: String? = null,
    val relatedVideos: List<Video> = emptyList()
)
