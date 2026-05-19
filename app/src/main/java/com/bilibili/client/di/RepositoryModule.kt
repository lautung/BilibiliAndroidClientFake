package com.bilibili.client.di

import com.bilibili.client.core.network.BiliApi
import com.bilibili.client.data.local.SettingsStore
import com.bilibili.client.data.repository.AuthRepositoryImpl
import com.bilibili.client.data.repository.LiveRepositoryImpl
import com.bilibili.client.data.repository.SearchRepositoryImpl
import com.bilibili.client.data.repository.UserRepositoryImpl
import com.bilibili.client.data.repository.VideoRepositoryImpl
import com.bilibili.client.domain.repository.AuthRepository
import com.bilibili.client.domain.repository.LiveRepository
import com.bilibili.client.domain.repository.SearchRepository
import com.bilibili.client.domain.repository.UserRepository
import com.bilibili.client.domain.repository.VideoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideVideoRepository(api: BiliApi): VideoRepository {
        return VideoRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideLiveRepository(api: BiliApi): LiveRepository {
        return LiveRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: BiliApi,
        settingsStore: SettingsStore
    ): AuthRepository {
        return AuthRepositoryImpl(api, settingsStore)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(api: BiliApi): SearchRepository {
        return SearchRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: BiliApi): UserRepository {
        return UserRepositoryImpl(api)
    }
}
