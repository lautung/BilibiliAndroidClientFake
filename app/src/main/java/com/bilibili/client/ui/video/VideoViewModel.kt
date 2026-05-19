package com.bilibili.client.ui.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideoInfo(
    val bvid: String,
    val title: String,
    val cover: String,
    val uploader: String,
    val uploaderMid: Long = 0,
    val views: String = "",
    val likes: String = "",
    val coins: String = "",
    val favorites: String = "",
    val description: String = ""
)

data class Comment(
    val id: Long,
    val username: String,
    val avatar: String = "",
    val content: String,
    val likes: String = "",
    val time: String = ""
)

data class VideoUiState(
    val videoInfo: VideoInfo? = null,
    val danmaku: List<DanmakuItem> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val relatedVideos: List<com.bilibili.client.ui.home.VideoItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPlayerReady: Boolean = false,
    val videoUrl: String? = null,
    val audioUrl: String? = null
)

data class DanmakuItem(
    val text: String,
    val timeMs: Long,
    val type: Int,
    val color: Long,
    val fontSize: Int
)

@HiltViewModel
class VideoViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    fun loadVideo(bvid: String) {
        viewModelScope.launch {
            _uiState.value = VideoUiState(isLoading = true)
            try {
                // TODO: API calls
                // 1. GET https://api.bilibili.com/x/web-interface/view?bvid={bvid}
                // 2. GET https://api.bilibili.com/x/player/playurl?bvid={bvid}&qn=80 (DASH URLs)
                // 3. GET https://api.bilibili.com/x/v2/dm/web/seg.so?oid={cid}&type=1&segment=1 (danmaku)
                // 4. GET https://api.bilibili.com/x/v2/medialist/resource/list?type=1&biz_id={bvid} (comments)
                _uiState.value = VideoUiState(
                    videoInfo = VideoInfo(
                        bvid = bvid,
                        title = "视频标题 - $bvid",
                        cover = "",
                        uploader = "UP主",
                        description = "视频描述..."
                    ),
                    isPlayerReady = true,
                    videoUrl = null,
                    audioUrl = null
                )
            } catch (e: Exception) {
                _uiState.value = VideoUiState(error = e.message)
            }
        }
    }
}
