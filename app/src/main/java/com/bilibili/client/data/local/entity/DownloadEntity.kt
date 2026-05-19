package com.bilibili.client.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bvid: String,
    val title: String,
    val cover: String,
    val quality: String,
    val progress: Float = 0f,
    val status: String = "PENDING",
    val filePath: String? = null
)
