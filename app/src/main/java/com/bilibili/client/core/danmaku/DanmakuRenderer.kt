package com.bilibili.client.core.danmaku

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.bilibili.client.domain.model.DanmakuItem
import com.bilibili.client.domain.model.DanmakuType

/**
 * Renders danmaku items onto a Canvas.
 * Supports rolling (right-to-left), top, and bottom danmaku types.
 */
class DanmakuRenderer(
    private val config: DanmakuConfig = DanmakuConfig()
) {

    private val textPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        typeface = Typeface.DEFAULT_BOLD
        textSize = config.fontSize
    }

    private val strokePaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.BLACK
    }

    data class DanmakuConfig(
        val fontSize: Float = 48f,
        val opacity: Float = 0.8f,
        val speedMultiplier: Float = 1.0f,
        val rollingLanes: Int = 5,
        val topBottomAreaRatio: Float = 0.4f
    )

    /**
     * Draws active danmaku items onto the canvas at their current positions.
     */
    fun draw(
        canvas: Canvas,
        activeItems: List<ActiveDanmaku>,
        width: Float,
        height: Float
    ) {
        for (item in activeItems) {
            drawItem(canvas, item, width, height)
        }
    }

    private fun drawItem(canvas: Canvas, active: ActiveDanmaku, width: Float, height: Float) {
        textPaint.color = active.item.color
        textPaint.alpha = (config.opacity * 255).toInt()
        textPaint.textSize = config.fontSize * (active.item.fontSize / 25f)

        val textWidth = textPaint.measureText(active.item.text)

        when (active.item.type) {
            DanmakuType.ROLLING -> {
                val y = active.lane?.let { lane ->
                    val laneHeight = (height * 0.9f) / config.rollingLanes
                    laneHeight * (lane + 1) - 8f
                } ?: (height * 0.3f)

                canvas.save()
                canvas.clipRect(0f, 0f, width, height * 0.9f)
                drawText(canvas, active.item.text, active.x, y)
                canvas.restore()
            }
            DanmakuType.TOP -> {
                val y = height * 0.1f + (height * config.topBottomAreaRatio) * ((active.id % 5) / 5f)
                drawText(canvas, active.item.text, width / 2 - textWidth / 2, y)
            }
            DanmakuType.BOTTOM -> {
                val y = height * 0.9f - (height * config.topBottomAreaRatio) * ((active.id % 5) / 5f)
                drawText(canvas, active.item.text, width / 2 - textWidth / 2, y)
            }
        }
    }

    private fun drawText(canvas: Canvas, text: String, x: Float, y: Float) {
        // Draw stroke (shadow) for readability
        canvas.drawText(text, x - 1, y - 1, strokePaint)
        canvas.drawText(text, x + 1, y - 1, strokePaint)
        canvas.drawText(text, x - 1, y + 1, strokePaint)
        canvas.drawText(text, x + 1, y + 1, strokePaint)
        // Draw fill
        canvas.drawText(text, x, y, textPaint)
    }
}

data class ActiveDanmaku(
    val item: DanmakuItem,
    var x: Float,
    var y: Float,
    val id: Long,
    var lane: Int? = null,
    var alive: Boolean = true
)
