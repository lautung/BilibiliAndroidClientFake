package com.bilibili.client.ui.video.components

import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.bilibili.client.core.danmaku.DanmakuEngine
import com.bilibili.client.core.danmaku.DanmakuOverlay
import com.bilibili.client.core.player.BiliPlayer

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    player: BiliPlayer,
    danmakuEngine: DanmakuEngine,
    modifier: Modifier = Modifier,
    playerPositionMs: Long = 0,
    showDanmaku: Boolean = true
) {
    Box(modifier = modifier.fillMaxWidth()) {
        // ExoPlayer Surface via PlayerView
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player.getExoPlayer()
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )

        // Danmaku overlay on top of player
        if (showDanmaku) {
            DanmakuOverlay(
                engine = danmakuEngine,
                playerPositionMs = playerPositionMs,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(horizontal = 4.dp),
                isVisible = true,
                opacity = 0.8f
            )
        }
    }
}
