package com.bilibili.client.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.client.data.local.SettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val followSystem: Boolean = true,
    val defaultQuality: String = "高清 1080P",
    val danmakuOpacity: Float = 0.8f,
    val danmakuSpeed: Float = 1.0f,
    val danmakuFontSize: Float = 25f
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsStore.isDarkMode.collect { dark ->
                _uiState.value = _uiState.value.copy(isDarkMode = dark)
            }
        }
        viewModelScope.launch {
            settingsStore.followSystem.collect { follow ->
                _uiState.value = _uiState.value.copy(followSystem = follow)
            }
        }
        viewModelScope.launch {
            settingsStore.danmakuOpacity.collect { opacity ->
                _uiState.value = _uiState.value.copy(danmakuOpacity = opacity)
            }
        }
        viewModelScope.launch {
            settingsStore.danmakuSpeed.collect { speed ->
                _uiState.value = _uiState.value.copy(danmakuSpeed = speed)
            }
        }
        viewModelScope.launch {
            settingsStore.danmakuFontSize.collect { size ->
                _uiState.value = _uiState.value.copy(danmakuFontSize = size)
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_uiState.value.isDarkMode
            settingsStore.setDarkMode(newValue)
            if (newValue) {
                settingsStore.setFollowSystem(false)
            }
        }
    }

    fun setFollowSystem(follow: Boolean) {
        viewModelScope.launch {
            settingsStore.setFollowSystem(follow)
        }
    }

    fun setDefaultQuality(quality: String) {
        viewModelScope.launch {
            settingsStore.setDefaultQuality(quality)
        }
    }

    fun setDanmakuOpacity(opacity: Float) {
        viewModelScope.launch {
            settingsStore.setDanmakuOpacity(opacity)
        }
    }

    fun setDanmakuSpeed(speed: Float) {
        viewModelScope.launch {
            settingsStore.setDanmakuSpeed(speed)
        }
    }

    fun setDanmakuFontSize(size: Float) {
        viewModelScope.launch {
            settingsStore.setDanmakuFontSize(size)
        }
    }
}
