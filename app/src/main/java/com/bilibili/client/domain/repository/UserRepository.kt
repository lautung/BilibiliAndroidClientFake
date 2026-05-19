package com.bilibili.client.domain.repository

import com.bilibili.client.domain.model.User

interface UserRepository {
    suspend fun getUserInfo(mid: Long): Result<User>
    suspend fun getUserVideos(mid: Long, page: Int = 1): Result<List<com.bilibili.client.domain.model.Video>>
}
