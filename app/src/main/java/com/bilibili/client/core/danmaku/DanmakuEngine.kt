package com.bilibili.client.core.danmaku

import com.bilibili.client.domain.model.DanmakuItem
import com.bilibili.client.domain.model.DanmakuType
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core danmaku engine that manages the lifecycle of danmaku items.
 */
@Singleton
class DanmakuEngine @Inject constructor() {

    private var allItems: List<DanmakuItem> = emptyList()
    private val activeItems = mutableListOf<ActiveDanmaku>()
    private var lastLaneUsage = mutableMapOf<Int, Long>() // lane -> last item time

    private var width: Float = 720f
    private var height: Float = 480f

    // Configuration
    var rollingLanes: Int = 5
        private set
    var speed: Float = 1.0f
        private set
    var isEnabled: Boolean = true

    // Speed of rolling danmaku in pixels per millisecond
    private val baseSpeed = 0.3f // px/ms
    private var frameTime: Long = 0

    fun setSize(w: Float, h: Float) {
        width = w
        height = h
    }

    fun loadDanmaku(items: List<DanmakuItem>) {
        allItems = items.sortedBy { it.timestampMs }
        activeItems.clear()
        lastLaneUsage.clear()
    }

    /**
     * Advance engine state to given player position (ms).
     * Returns active items for the current frame.
     */
    fun advanceTo(playerPositionMs: Long, deltaMs: Long): List<ActiveDanmaku> {
        if (!isEnabled) return emptyList()
        frameTime = playerPositionMs

        // Emit new items whose timestamp has passed
        val newItems = allItems.filter { item ->
            item.timestampMs in (playerPositionMs - deltaMs - 100)..playerPositionMs &&
                activeItems.none { it.item.id == item.id }
        }

        for (item in newItems) {
            spawnItem(item)
        }

        // Update positions of active items
        val toRemove = mutableListOf<ActiveDanmaku>()
        for (active in activeItems) {
            updatePosition(active, deltaMs)
            if (!active.alive) {
                toRemove.add(active)
            }
        }

        activeItems.removeAll(toRemove)

        return activeItems.toList()
    }

    private fun spawnItem(item: DanmakuItem) {
        when (item.type) {
            DanmakuType.ROLLING -> {
                val lane = assignLane()
                val startX = width
                val y = lane * (height * 0.9f / rollingLanes) + 32f
                activeItems.add(
                    ActiveDanmaku(
                        item = item,
                        x = startX,
                        y = y,
                        id = item.id,
                        lane = lane,
                        alive = true
                    )
                )
            }
            DanmakuType.TOP -> {
                val y = height * 0.1f + (item.id % 5) * 32f
                activeItems.add(
                    ActiveDanmaku(
                        item = item,
                        x = width / 2f,
                        y = y,
                        id = item.id,
                        alive = true
                    )
                )
                activeItems.add(
                    ActiveDanmaku(
                        item = item,
                        x = width / 2f,
                        y = y + height * 0.05f,
                        id = item.id + 1000000,
                        alive = true
                    )
                )
            }
            DanmakuType.BOTTOM -> {
                activeItems.add(
                    ActiveDanmaku(
                        item = item,
                        x = width / 2f,
                        y = height * 0.85f,
                        id = item.id,
                        alive = true
                    )
                )
            }
        }
    }

    private fun updatePosition(active: ActiveDanmaku, deltaMs: Long) {
        when (active.item.type) {
            DanmakuType.ROLLING -> {
                active.x -= baseSpeed * speed * deltaMs
                // Mark as dead when fully off-screen
                val textWidthMeasure = active.item.text.length * 18f
                if (active.x + textWidthMeasure < 0) {
                    active.alive = false
                }
            }
            DanmakuType.TOP, DanmakuType.BOTTOM -> {
                // Top/bottom items: stay visible for 4 seconds then fade
                if (active.item.timestampMs + 4000 < frameTime) {
                    active.alive = false
                }
            }
        }
    }

    private fun assignLane(): Int {
        // Find the lane whose last item is furthest to the right (oldest)
        // Simple approach: pick the least recently used lane
        val now = System.nanoTime()
        var bestLane = 0
        var oldestTime = Long.MAX_VALUE

        for (lane in 0 until rollingLanes) {
            val lastUsed = lastLaneUsage[lane] ?: 0L
            if (lastUsed < oldestTime) {
                oldestTime = lastUsed
                bestLane = lane
            }
        }

        lastLaneUsage[bestLane] = now
        return bestLane
    }

    /**
     * Clear all items (for video transitions).
     */
    fun clear() {
        allItems = emptyList()
        activeItems.clear()
        lastLaneUsage.clear()
    }

    fun setRollingLanes(lanes: Int) {
        rollingLanes = lanes.coerceIn(1, 10)
    }

    fun setSpeed(s: Float) {
        speed = s.coerceIn(0.5f, 3f)
    }
}
