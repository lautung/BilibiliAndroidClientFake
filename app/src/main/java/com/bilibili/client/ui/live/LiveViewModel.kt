package com.bilibili.client.ui.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.client.core.danmaku.DanmakuEngine
import com.bilibili.client.core.player.BiliPlayer
import com.bilibili.client.domain.repository.LiveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val roomTitle: String = "",
    val uploader: String = "",
    val viewerCount: Long = 0,
    val isLive: Boolean = false,
    val playUrl: String? = null,
    val isPlayerReady: Boolean = false,
    val coverUrl: String = ""
)

@HiltViewModel
class LiveViewModel @Inject constructor(
    private val liveRepository: LiveRepository,
    val biliPlayer: BiliPlayer,
    val danmakuEngine: DanmakuEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveUiState())
    val uiState: StateFlow<LiveUiState> = _uiState.asStateFlow()

    fun loadRoom(roomId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val roomsResult = liveRepository.getLiveRooms()
                val room = roomsResult.getOrNull()?.find { it.roomId == roomId }

                val playUrlResult = liveRepository.getLivePlayUrl(roomId)
                val playUrl = playUrlResult.getOrNull()

                _uiState.value = LiveUiState(
                    roomTitle = room?.title ?: "直播",
                    uploader = room?.uploader ?: "",
                    viewerCount = room?.viewerCount ?: 0,
                    isLive = room?.isLive ?: false,
                    playUrl = playUrl?.url,
                    isPlayerReady = playUrl != null,
                    coverUrl = room?.coverUrl ?: ""
                )

                playUrl?.let {
                    if (it.url.isNotEmpty()) {
                        biliPlayer.prepare(it.url)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        biliPlayer.release()
    }
}
