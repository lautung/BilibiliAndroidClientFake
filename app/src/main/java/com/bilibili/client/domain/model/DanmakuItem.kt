package com.bilibili.client.domain.model

data class DanmakuItem(
    val id: Long,
    val text: String,
    val timestampMs: Long,
    val type: DanmakuType,
    val color: Int,
    val fontSize: Float
)

enum class DanmakuType {
    ROLLING,   // 滚动弹幕
    TOP,       // 顶部弹幕
    BOTTOM     // 底部弹幕
}
