package com.bilibili.client.ui.creator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.client.domain.model.Video
import com.bilibili.client.domain.repository.UserRepository
import com.bilibili.client.ui.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreatorUiState(
    val userName: String = "",
    val avatar: String = "",
    val followerCount: Long = 0,
    val videoCount: Long = 0,
    val videos: List<Video> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CreatorViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatorUiState())
    val uiState: StateFlow<CreatorUiState> = _uiState.asStateFlow()

    fun loadCreator(mid: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val userResult = userRepository.getUserInfo(mid)
                val videosResult = userRepository.getUserVideos(mid)

                val user = userResult.getOrNull()
                val videos = videosResult.getOrNull() ?: emptyList()

                _uiState.value = CreatorUiState(
                    userName = user?.name ?: "UP主",
                    avatar = user?.avatar ?: "",
                    followerCount = user?.followerCount ?: 0,
                    videoCount = user?.videoCount ?: 0,
                    videos = videos
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
