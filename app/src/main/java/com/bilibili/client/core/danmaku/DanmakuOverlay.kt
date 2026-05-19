package com.bilibili.client.core.danmaku

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.delay

/**
 * Composable that renders danmaku (bullet comments) overlaid on the video player.
 * Uses a Compose Canvas with native Android canvas text drawing for performance.
 */
@Composable
fun DanmakuOverlay(
    engine: DanmakuEngine,
    playerPositionMs: Long,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    opacity: Float = 0.8f,
    fontSize: Float = 42f
) {
    if (!isVisible) return

    var activeItems by remember { mutableStateOf<List<ActiveDanmaku>>(emptyList()) }
    var lastPositionMs by remember { mutableLongStateOf(0L) }

    // Paints cached across recompositions
    val textPaint = remember {
        Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
            typeface = Typeface.DEFAULT_BOLD
            textSize = fontSize
        }
    }

    val strokePaint = remember {
        Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = android.graphics.Color.BLACK
        }
    }

    // Advance danmaku engine based on player position
    LaunchedEffect(playerPositionMs) {
        val deltaMs = if (lastPositionMs == 0L) 16L else (playerPositionMs - lastPositionMs).coerceIn(8L, 100L)
        activeItems = engine.advanceTo(playerPositionMs, deltaMs)
        lastPositionMs = playerPositionMs
    }

    // Frame tick to trigger recomposition
    var frameTick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // ~60fps
            frameTick++
        }
    }

    Canvas(modifier = modifier) {
        engine.setSize(size.width, size.height)

        val canvas = drawContext.canvas.nativeCanvas

        // Update text paint size
        textPaint.textSize = fontSize

        for (active in activeItems) {
            val alpha = (opacity * 255).toInt()
            val argb = active.item.color
            textPaint.color = argb
            textPaint.alpha = alpha

            val textSize = fontSize * (active.item.fontSize / 25f)
            textPaint.textSize = textSize

            val text = active.item.text

            when (active.item.type) {
                com.bilibili.client.domain.model.DanmakuType.ROLLING -> {
                    canvas.drawText(text, active.x, active.y, strokePaint)
                    canvas.drawText(text, active.x, active.y, textPaint)
                }
                com.bilibili.client.domain.model.DanmakuType.TOP -> {
                    canvas.drawText(text, active.x, active.y, strokePaint)
                    canvas.drawText(text, active.x, active.y, textPaint)
                }
                com.bilibili.client.domain.model.DanmakuType.BOTTOM -> {
                    canvas.drawText(text, active.x, active.y, strokePaint)
                    canvas.drawText(text, active.x, active.y, textPaint)
                }
            }
        }
    }
}
