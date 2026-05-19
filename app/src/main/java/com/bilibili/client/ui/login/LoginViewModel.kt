package com.bilibili.client.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.client.core.network.AuthInterceptor
import com.bilibili.client.data.local.SettingsStore
import com.bilibili.client.domain.repository.AuthRepository
import com.bilibili.client.domain.repository.QrStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val qrCodeUrl: String = "",
    val qrCodeKey: String = "",
    val isLoading: Boolean = false,
    val isPolling: Boolean = false,
    val scanStatus: String? = null,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsStore: SettingsStore,
    private val authInterceptor: AuthInterceptor
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        generateQRCode()
    }

    fun generateQRCode() {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            authRepository.getQrCode()
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        qrCodeUrl = result.url,
                        qrCodeKey = result.qrcodeKey,
                        isLoading = false,
                        error = null
                    )
                    startPolling()
                }
                .onFailure { e ->
                    _uiState.value = LoginUiState(
                        isLoading = false,
                        error = e.message ?: "获取二维码失败"
                    )
                }
        }
    }

    private fun startPolling() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPolling = true)
            val key = _uiState.value.qrCodeKey
            var attempts = 0

            while (attempts < 300) { // 300 × 2s = 10 minutes
                delay(2000)
                attempts++

                authRepository.pollQrStatus(key)
                    .onSuccess { status ->
                        when (status) {
                            QrStatus.SCANNED -> {
                                _uiState.value = _uiState.value.copy(scanStatus = "已扫描，请在手机上确认")
                            }
                            QrStatus.CONFIRMED -> {
                                _uiState.value = _uiState.value.copy(
                                    scanStatus = "登录成功！",
                                    isLoggedIn = true,
                                    isPolling = false
                                )
                                // Save session and update interceptor
                                onLoginConfirmed()
                                delay(1000)
                                return@launch
                            }
                            QrStatus.EXPIRED -> {
                                _uiState.value = _uiState.value.copy(
                                    scanStatus = "二维码已过期，请刷新",
                                    isPolling = false
                                )
                                return@launch
                            }
                            QrStatus.WAITING -> {
                                _uiState.value = _uiState.value.copy(scanStatus = "等待扫码...")
                            }
                        }
                    }
                    .onFailure { /* continue polling */ }
            }
        }
    }

    private suspend fun onLoginConfirmed() {
        authRepository.getCurrentUser()
            .onSuccess { user ->
                // Session cookies should be set by network layer
                // Update interceptor state
                val sessdata = settingsStore.getSessdata()
                val biliJct = settingsStore.getBiliJct()
                if (!sessdata.isNullOrEmpty() && !biliJct.isNullOrEmpty()) {
                    authInterceptor.setSession(sessdata, biliJct)
                }
            }
    }
}
