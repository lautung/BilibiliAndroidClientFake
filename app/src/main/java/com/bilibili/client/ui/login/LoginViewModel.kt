package com.bilibili.client.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val qrCodeUrl: String? = null,
    val qrCode: String? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val scanStatus: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        generateQRCode()
    }

    fun generateQRCode() {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            // TODO: Implement Bilibili QR code login API
            // 1. GET https://passport.bilibili.com/x/passport-login/web/qrcode/generate
            // 2. Display QR code
            // 3. Poll https://passport.bilibili.com/x/passport-login/web/qrcode/poll
            // 4. On success, store SESSDATA in EncryptedSharedPreferences
            _uiState.value = LoginUiState(
                qrCodeUrl = "https://passport.bilibili.com/x/passport-login/web/qrcode/generate"
            )
        }
    }

    fun checkLoginStatus() {
        // TODO: Check if SESSDATA exists and is valid
    }
}
