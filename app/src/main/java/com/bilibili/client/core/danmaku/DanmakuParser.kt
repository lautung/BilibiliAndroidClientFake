package com.bilibili.client.core.danmaku

import com.bilibili.client.domain.model.DanmakuItem
import com.bilibili.client.domain.model.DanmakuType
import java.io.BufferedReader
import java.io.StringReader

/**
 * Parses Bilibili danmaku XML/protobuf into DanmakuItem domain models.
 * Supports the legacy XML format (seg.so returns protobuf in newer API, but
 * the XML parser handles the older format and some fallback endpoints).
 */
object DanmakuParser {

    /**
     * Parse standard Bilibili danmaku XML format.
     * Format: <d p="timestamp,type,fontSize,color,pool,user,row">text</d>
     */
    fun parseXml(xml: String): List<DanmakuItem> {
        val result = mutableListOf<DanmakuItem>()
        val regex = Regex("<d p=\"([^\"]+)\"[^>]*>([^<]*)</d>")

        var id = 0L
        for (match in regex.findAll(xml)) {
            val params = match.groupValues[1].split(",")
            val text = match.groupValues[2].trim()
            if (text.isBlank()) continue

            val timestampSeconds = params.getOrNull(0)?.toFloatOrNull() ?: 0f
            val rawType = params.getOrNull(1)?.toIntOrNull() ?: 1
            val fontSize = params.getOrNull(2)?.toIntOrNull() ?: 25
            val colorHex = params.getOrNull(3)?.toLongOrNull() ?: 0xFFFFFFL

            val type = when (rawType) {
                1 -> DanmakuType.ROLLING
                4 -> DanmakuType.BOTTOM
                5 -> DanmakuType.TOP
                else -> DanmakuType.ROLLING
            }

            result.add(
                DanmakuItem(
                    id = id++,
                    text = text,
                    timestampMs = (timestampSeconds * 1000).toLong(),
                    type = type,
                    color = (0xFF000000 or colorHex).toInt(), // ensure alpha
                    fontSize = if (fontSize <= 0) 25f else fontSize.toFloat()
                )
            )
        }
        return result
    }

    /**
     * Parse protobuf bytes into danmaku items.
     * Bilibili uses bilibili.dm.protobuf.DanmakuElem protobuf format.
     * For now, falls back to XML since we request seg.so.
     */
    fun parseProtobuf(data: ByteArray): List<DanmakuItem> {
        // TODO: Implement protobuf parsing using the Bilibili protobuf schema
        // For now, return empty — the danmaku endpoint returns protobuf,
        // but we can use the XML fallback endpoint instead.
        return emptyList()
    }
}
