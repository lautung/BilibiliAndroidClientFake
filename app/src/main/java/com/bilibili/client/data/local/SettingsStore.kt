package com.bilibili.client.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsStore(private val context: Context) {

    companion object {
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val FOLLOW_SYSTEM = booleanPreferencesKey("follow_system")
        private val DEFAULT_QUALITY = stringPreferencesKey("default_quality")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }
    val followSystem: Flow<Boolean> = context.dataStore.data.map { it[FOLLOW_SYSTEM] ?: true }
    val defaultQuality: Flow<String> = context.dataStore.data.map { it[DEFAULT_QUALITY] ?: "高清 1080P" }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }

    suspend fun setFollowSystem(follow: Boolean) {
        context.dataStore.edit { it[FOLLOW_SYSTEM] = follow }
    }

    suspend fun setDefaultQuality(quality: String) {
        context.dataStore.edit { it[DEFAULT_QUALITY] = quality }
    }
}
