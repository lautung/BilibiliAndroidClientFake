package com.bilibili.client.domain.model

data class User(
    val mid: Long,
    val name: String,
    val avatar: String,
    val sign: String,
    val level: Int,
    val followerCount: Long,
    val followingCount: Long,
    val videoCount: Long,
    val isFollowed: Boolean = false
)
