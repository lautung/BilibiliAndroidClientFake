package com.bilibili.client.di

import android.content.Context
import com.bilibili.client.data.local.SettingsStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsStore(
        @ApplicationContext context: Context
    ): SettingsStore {
        return SettingsStore(context)
    }
}
