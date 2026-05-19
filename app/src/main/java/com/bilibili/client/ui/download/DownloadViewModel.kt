package com.bilibili.client.ui.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadTask(
    val id: Long,
    val title: String,
    val cover: String,
    val progress: Float = 0f,
    val status: DownloadStatus = DownloadStatus.PENDING
)

enum class DownloadStatus {
    PENDING, DOWNLOADING, PAUSED, COMPLETED, FAILED
}

data class DownloadUiState(
    val downloads: List<DownloadTask> = emptyList(),
    val isServerRunning: Boolean = false,
    val serverUrl: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class DownloadViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadUiState())
    val uiState: StateFlow<DownloadUiState> = _uiState.asStateFlow()

    fun toggleServer() {
        if (_uiState.value.isServerRunning) {
            stopServer()
        } else {
            startServer()
        }
    }

    private fun startServer() {
        viewModelScope.launch {
            // TODO: Start NanoHTTPD server on local network
            _uiState.value = _uiState.value.copy(
                isServerRunning = true,
                serverUrl = "http://192.168.1.100:8080"
            )
        }
    }

    private fun stopServer() {
        _uiState.value = _uiState.value.copy(isServerRunning = false, serverUrl = null)
    }
}
