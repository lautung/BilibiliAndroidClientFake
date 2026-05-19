package com.bilibili.client.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache")
data class CacheEntity(
    @PrimaryKey
    val key: String,
    val data: String,
    val timestamp: Long = System.currentTimeMillis()
)
