package com.bilibili.client.domain.repository

interface SearchRepository {
    suspend fun searchVideos(keyword: String, page: Int = 1): Result<SearchResult>
    suspend fun getSuggestions(keyword: String): Result<List<String>>
}
