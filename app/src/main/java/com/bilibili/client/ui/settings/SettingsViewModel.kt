package com.bilibili.client.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val followSystem: Boolean = true,
    val defaultQuality: String = "高清 1080P",
    val danmakuOpacity: Float = 0.8f,
    val danmakuSpeed: Float = 1.0f,
    val danmakuFontSize: Float = 1.0f
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleDarkMode() {
        _uiState.value = _uiState.value.copy(
            isDarkMode = !_uiState.value.isDarkMode,
            followSystem = false
        )
        // TODO: Persist to DataStore
    }

    fun setFollowSystem(follow: Boolean) {
        _uiState.value = _uiState.value.copy(followSystem = follow)
    }

    fun setDefaultQuality(quality: String) {
        _uiState.value = _uiState.value.copy(defaultQuality = quality)
    }
}
