package com.bilibili.client.data.repository

import com.bilibili.client.core.network.BiliApi
import com.bilibili.client.domain.repository.SearchRepository
import com.bilibili.client.domain.repository.SearchResult
import com.bilibili.client.domain.repository.SearchUserItem
import com.bilibili.client.domain.repository.SearchVideoItem
import com.bilibili.client.domain.repository.UserRepository
import com.bilibili.client.data.model.toDomainVideo
import com.bilibili.client.domain.model.User
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: BiliApi
) : SearchRepository {

    override suspend fun searchVideos(keyword: String, page: Int): Result<SearchResult> = runCatching {
        val response = api.searchVideos(keyword = keyword, page = page).body() ?: throw Exception("Empty response")
        if (response.code != 0) throw Exception(response.message)
        val data = response.data ?: throw Exception("Search data not available")

        SearchResult(
            videos = data.result.map { dto ->
                SearchVideoItem(
                    bvid = dto.bvid,
                    title = dto.title,
                    coverUrl = dto.pic,
                    uploader = dto.author,
                    views = dto.play,
                    duration = dto.duration
                )
            },
            users = emptyList(),
            totalResults = data.numResults.toInt()
        )
    }

    override suspend fun getSuggestions(keyword: String): Result<List<String>> = runCatching {
        val response = api.getSearchSuggestions(term = keyword).body() ?: throw Exception("Empty response")
        response.data?.suggest ?: emptyList()
    }
}

class UserRepositoryImpl @Inject constructor(
    private val api: BiliApi
) : UserRepository {

    override suspend fun getUserInfo(mid: Long): Result<User> = runCatching {
        val response = api.getUserInfo(mid = mid).body() ?: throw Exception("Empty response")
        if (response.code != 0) throw Exception(response.message)
        val data = response.data ?: throw Exception("User data not available")
        User(
            mid = data.mid,
            name = data.name,
            avatar = data.face ?: "",
            sign = "",
            level = 0,
            followerCount = 0,
            followingCount = 0,
            videoCount = 0
        )
    }

    override suspend fun getUserVideos(mid: Long, page: Int): Result<List<com.bilibili.client.domain.model.Video>> = runCatching {
        val response = api.getCreatorVideos(mid = mid, page = page).body() ?: throw Exception("Empty response")
        if (response.code != 0) throw Exception(response.message)
        response.data?.list?.map { it.toDomainVideo() } ?: emptyList()
    }
}
