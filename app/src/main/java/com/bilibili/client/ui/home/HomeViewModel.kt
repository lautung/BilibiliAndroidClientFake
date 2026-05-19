package com.bilibili.client.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.client.domain.repository.AuthRepository
import com.bilibili.client.domain.repository.LiveRepository
import com.bilibili.client.domain.repository.VideoRepository
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
    val duration: String,
    val uploaderAvatar: String = ""
)

data class LiveRoomItem(
    val roomId: Long,
    val title: String,
    val cover: String,
    val uploader: String,
    val viewerCount: String,
    val isLiving: Boolean = true,
    val uploaderAvatar: String = ""
)

data class HomeUiState(
    val hotVideos: List<VideoItem> = emptyList(),
    val liveRooms: List<LiveRoomItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val liveRepository: LiveRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        loadData()
        checkLoginStatus()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        loadData()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoggedIn.value = authRepository.isLoggedIn()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val hotResult = videoRepository.getHotVideos()
                val liveResult = liveRepository.getLiveRooms()

                val hotVideos = hotResult.getOrNull()?.map { video ->
                    VideoItem(
                        bvid = video.bvid,
                        title = video.title,
                        cover = video.coverUrl,
                        uploader = video.uploader,
                        views = formatCount(video.views),
                        duration = video.duration,
                        uploaderAvatar = video.uploaderAvatar
                    )
                } ?: emptyList()

                val liveRooms = liveResult.getOrNull()?.map { room ->
                    LiveRoomItem(
                        roomId = room.roomId,
                        title = room.title,
                        cover = room.coverUrl,
                        uploader = room.uploader,
                        viewerCount = formatCount(room.viewerCount),
                        isLiving = room.isLive,
                        uploaderAvatar = room.uploaderAvatar
                    )
                } ?: emptyList()

                _uiState.value = HomeUiState(
                    hotVideos = hotVideos,
                    liveRooms = liveRooms,
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

    companion object {
        fun formatCount(count: Long): String = when {
            count >= 10000 -> "${count / 10000}万"
            count >= 1000 -> "${count / 1000}千"
            else -> count.toString()
        }
    }
}
