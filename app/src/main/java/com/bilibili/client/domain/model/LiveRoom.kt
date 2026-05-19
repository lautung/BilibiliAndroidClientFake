package com.bilibili.client.domain.model

data class LiveRoom(
    val roomId: Long,
    val uid: Long,
    val title: String,
    val uploader: String,
    val uploaderAvatar: String,
    val viewerCount: Long,
    val coverUrl: String,
    val key: String?,
    val playUrl: String?,
    val isLive: Boolean = true
)
