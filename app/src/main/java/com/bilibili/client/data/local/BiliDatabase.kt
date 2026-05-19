package com.bilibili.client.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bilibili.client.data.local.dao.CacheDao
import com.bilibili.client.data.local.dao.DownloadDao
import com.bilibili.client.data.local.entity.CacheEntity
import com.bilibili.client.data.local.entity.DownloadEntity

@Database(
    entities = [DownloadEntity::class, CacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BiliDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
    abstract fun cacheDao(): CacheDao
}
