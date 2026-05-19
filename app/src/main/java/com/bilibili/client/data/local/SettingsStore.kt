package com.bilibili.client.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsStore(private val context: Context) {

    companion object {
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val FOLLOW_SYSTEM = booleanPreferencesKey("follow_system")
        private val DEFAULT_QUALITY = stringPreferencesKey("default_quality")
        private val SESSDATA = stringPreferencesKey("sessdata")
        private val BILI_JCT = stringPreferencesKey("bili_jct")
        private val USER_ID = stringPreferencesKey("user_id")
        private val DANMAKU_OPACITY = floatPreferencesKey("danmaku_opacity")
        private val DANMAKU_SPEED = floatPreferencesKey("danmaku_speed")
        private val DANMAKU_FONT_SIZE = floatPreferencesKey("danmaku_font_size")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }
    val followSystem: Flow<Boolean> = context.dataStore.data.map { it[FOLLOW_SYSTEM] ?: true }
    val defaultQuality: Flow<String> = context.dataStore.data.map { it[DEFAULT_QUALITY] ?: "高清 1080P" }

    val danmakuOpacity: Flow<Float> = context.dataStore.data.map { it[DANMAKU_OPACITY] ?: 0.8f }
    val danmakuSpeed: Flow<Float> = context.dataStore.data.map { it[DANMAKU_SPEED] ?: 1.0f }
    val danmakuFontSize: Flow<Float> = context.dataStore.data.map { it[DANMAKU_FONT_SIZE] ?: 25f }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }

    suspend fun setFollowSystem(follow: Boolean) {
        context.dataStore.edit { it[FOLLOW_SYSTEM] = follow }
    }

    suspend fun setDefaultQuality(quality: String) {
        context.dataStore.edit { it[DEFAULT_QUALITY] = quality }
    }

    suspend fun setDanmakuOpacity(opacity: Float) {
        context.dataStore.edit { it[DANMAKU_OPACITY] = opacity }
    }

    suspend fun setDanmakuSpeed(speed: Float) {
        context.dataStore.edit { it[DANMAKU_SPEED] = speed }
    }

    suspend fun setDanmakuFontSize(size: Float) {
        context.dataStore.edit { it[DANMAKU_FONT_SIZE] = size }
    }

    suspend fun saveSession(sessdata: String, biliJct: String, userId: String) {
        context.dataStore.edit {
            it[SESSDATA] = sessdata
            it[BILI_JCT] = biliJct
            it[USER_ID] = userId
        }
    }

    suspend fun clearAuth() {
        context.dataStore.edit {
            it.remove(SESSDATA)
            it.remove(BILI_JCT)
            it.remove(USER_ID)
        }
    }

    suspend fun hasSession(): Boolean {
        return context.dataStore.data.first()[SESSDATA]?.isNotEmpty() == true
    }

    suspend fun getSessdata(): String? {
        return context.dataStore.data.first()[SESSDATA]
    }

    suspend fun getBiliJct(): String? {
        return context.dataStore.data.first()[BILI_JCT]
    }
}
