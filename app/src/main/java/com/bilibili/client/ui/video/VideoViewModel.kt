package com.bilibili.client.ui.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.client.core.danmaku.DanmakuEngine
import com.bilibili.client.core.player.BiliPlayer
import com.bilibili.client.domain.repository.VideoRepository
import com.bilibili.client.ui.home.VideoItem
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

data class CommentItem(
    val id: Long,
    val username: String,
    val avatar: String = "",
    val content: String,
    val likes: String = "",
    val time: String = ""
)

data class VideoUiState(
    val videoInfo: VideoInfo? = null,
    val comments: List<CommentItem> = emptyList(),
    val relatedVideos: List<VideoItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPlayerReady: Boolean = false,
    val videoUrl: String? = null,
    val audioUrl: String? = null,
    val playerPositionMs: Long = 0
)

@HiltViewModel
class VideoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val videoRepository: VideoRepository,
    val biliPlayer: BiliPlayer,
    val danmakuEngine: DanmakuEngine
) : ViewModel() {

    private val bvid: String = savedStateHandle["bvid"] ?: ""

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    init {
        if (bvid.isNotBlank()) {
            loadVideo(bvid)
        }
    }

    fun loadVideo(bvid: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Load video detail
                val detailResult = videoRepository.getVideoDetail(bvid)
                val detail = detailResult.getOrNull()
                val cid = detailResult.getOrNull()?.let { 0L } ?: 0L
                // Note: We need cid from detail, but our domain model doesn't expose it yet
                // For now, extract from the play URL API response

                val playUrlResult = videoRepository.getPlayUrl(bvid, cid = 0, quality = 80)
                val playUrl = playUrlResult.getOrNull()

                // Load danmaku
                val danmakuResult = videoRepository.getDanmaku(cid = 0)
                danmakuResult.getOrNull()?.let { danmaku ->
                    danmakuEngine.loadDanmaku(danmaku)
                }

                // Load comments
                val commentResult = videoRepository.getComments(bvid)

                // Load related videos
                val relatedResult = videoRepository.getRelatedVideos(bvid)

                val video = detail
                _uiState.value = VideoUiState(
                    videoInfo = video?.let {
                        VideoInfo(
                            bvid = it.bvid,
                            title = it.title,
                            cover = it.coverUrl,
                            uploader = it.uploader,
                            uploaderMid = it.uploaderMid,
                            views = formatCount(it.views),
                            likes = formatCount(it.likes),
                            coins = formatCount(it.coins),
                            favorites = formatCount(it.favorites),
                            description = it.description
                        )
                    },
                    comments = commentResult.getOrNull()?.comments?.map { c ->
                        CommentItem(
                            id = c.id,
                            username = c.userName,
                            avatar = c.userAvatar,
                            content = c.content,
                            likes = formatCount(c.likes.toLong()),
                            time = formatTimestamp(c.pubdate)
                        )
                    } ?: emptyList(),
                    relatedVideos = relatedResult.getOrNull()?.map { v ->
                        VideoItem(
                            bvid = v.bvid,
                            title = v.title,
                            cover = v.coverUrl,
                            uploader = v.uploader,
                            views = formatCount(v.views),
                            duration = v.duration
                        )
                    } ?: emptyList(),
                    isPlayerReady = playUrl != null,
                    videoUrl = playUrl?.url,
                    audioUrl = playUrl?.dashAudio
                )

                // Start preparing player
                playUrl?.let { result ->
                    biliPlayer.prepare(result.url, result.dashAudio)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun play() {
        biliPlayer.play()
    }

    fun pause() {
        biliPlayer.pause()
    }

    fun seekTo(positionMs: Long) {
        biliPlayer.seekTo(positionMs)
    }

    fun togglePlayPause() {
        if (biliPlayer.state.value.isPlaying) biliPlayer.pause()
        else biliPlayer.play()
    }

    override fun onCleared() {
        super.onCleared()
        biliPlayer.release()
    }

    companion object {
        fun formatCount(count: Long): String = when {
            count >= 10000 -> "${count / 10000}万"
            count >= 1000 -> "${count / 1000}千"
            else -> count.toString()
        }

        fun formatTimestamp(seconds: Long): String {
            if (seconds <= 0) return ""
            // seconds is typically unix timestamp
            return "刚刚"
        }
    }
}
