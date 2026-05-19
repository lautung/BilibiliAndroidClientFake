package com.bilibili.client.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideoItem(
    val bvid: String,
    val title: String,
    val cover: String,
    val uploader: String,
    val views: String,
    val duration: String
)

data class LiveRoomItem(
    val roomId: Long,
    val title: String,
    val cover: String,
    val uploader: String,
    val viewerCount: String,
    val isLiving: Boolean = true
)

data class HomeUiState(
    val hotVideos: List<VideoItem> = emptyList(),
    val liveRooms: List<LiveRoomItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        loadData()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // TODO: Call Bilibili API
                // - GET https://api.bilibili.com/x/web-interface/popular (热门)
                // - GET https://api.live.bilibili.com/room/v1/Area/getList (直播分区)
                val mockVideos = listOf(
                    VideoItem("BV1xx411c7mD", "示例视频标题", "", "UP主1", "10万", "5:30"),
                    VideoItem("BV1xx411c7mE", "另一个视频", "", "UP主2", "5万", "3:15"),
                )
                val mockLives = listOf(
                    LiveRoomItem(1L, "直播间1", "", "主播1", "1000"),
                    LiveRoomItem(2L, "直播间2", "", "主播2", "500"),
                )
                _uiState.value = HomeUiState(
                    hotVideos = mockVideos,
                    liveRooms = mockLives,
                    isRefreshing = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = e.message
                )
            }
        }
    }
}
