package com.bilibili.client.di

import com.bilibili.client.data.local.BiliDatabase
import com.bilibili.client.data.local.dao.CacheDao
import com.bilibili.client.data.local.dao.DownloadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        // context: Context
    ): BiliDatabase {
        // TODO: Room.databaseBuilder(context, BiliDatabase::class.java, "bilibili.db").build()
        throw NotImplementedError("Room database not yet initialized")
    }

    @Provides
    fun provideDownloadDao(database: BiliDatabase): DownloadDao = database.downloadDao()

    @Provides
    fun provideCacheDao(database: BiliDatabase): CacheDao = database.cacheDao()
}
