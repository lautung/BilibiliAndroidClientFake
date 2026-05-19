package com.bilibili.client.core.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val bufferedPosition: Long = 0,
    val isBuffering: Boolean = false,
    val volume: Float = 1f,
    val playbackSpeed: Float = 1f,
    val quality: Int = 80,
    val isPrepared: Boolean = false
)

@OptIn(UnstableApi::class)
@Singleton
class BiliPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .build()

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.value = _state.value.copy(isPlaying = isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _state.value = _state.value.copy(
                isPrepared = playbackState != Player.STATE_IDLE,
                isBuffering = playbackState == Player.STATE_BUFFERING
            )
        }

        override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
            updatePosition()
        }
    }

    init {
        exoPlayer.addListener(listener)
    }

    fun prepare(url: String, dashAudioUrl: String? = null) {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        val source = if (url.contains(".mpd")) {
            DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(url))
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(url))
        }

        exoPlayer.setMediaSource(source)
        exoPlayer.prepare()
        _state.value = _state.value.copy(duration = 0)
    }

    fun play() {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        updatePosition()
    }

    fun setVolume(volume: Float) {
        exoPlayer.volume = volume.coerceIn(0f, 1f)
        _state.value = _state.value.copy(volume = exoPlayer.volume)
    }

    var currentPlaybackSpeed: Float = 1f
        private set

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer.setPlaybackSpeed(speed.coerceIn(0.5f, 2f))
        currentPlaybackSpeed = speed.coerceIn(0.5f, 2f)
        _state.value = _state.value.copy(playbackSpeed = currentPlaybackSpeed)
    }

    fun getCurrentPosition(): Long = exoPlayer.currentPosition

    fun getExoPlayer(): ExoPlayer = exoPlayer

    fun release() {
        exoPlayer.removeListener(listener)
        exoPlayer.release()
    }

    private fun updatePosition() {
        _state.value = _state.value.copy(
            currentPosition = exoPlayer.currentPosition,
            duration = exoPlayer.duration.coerceAtLeast(0),
            bufferedPosition = exoPlayer.bufferedPosition.coerceAtLeast(0)
        )
    }
}
