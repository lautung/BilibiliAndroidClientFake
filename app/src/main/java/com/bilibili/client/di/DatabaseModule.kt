package com.bilibili.client.di

import android.content.Context
import androidx.room.Room
import com.bilibili.client.data.local.BiliDatabase
import com.bilibili.client.data.local.dao.CacheDao
import com.bilibili.client.data.local.dao.DownloadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BiliDatabase {
        return Room.databaseBuilder(
            context,
            BiliDatabase::class.java,
            "bilibili.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideDownloadDao(database: BiliDatabase): DownloadDao = database.downloadDao()

    @Provides
    fun provideCacheDao(database: BiliDatabase): CacheDao = database.cacheDao()
}
