package com.bilibili.client.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilibili.client.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchResult(
    val bvid: String,
    val title: String,
    val cover: String,
    val uploader: String,
    val views: String,
    val duration: String
)

data class SearchUiState(
    val query: String = "",
    val suggestions: List<String> = emptyList(),
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(300) // debounce
                searchRepository.getSuggestions(query)
                    .onSuccess { suggestions ->
                        _uiState.value = _uiState.value.copy(suggestions = suggestions)
                    }
            }
        } else {
            _uiState.value = _uiState.value.copy(suggestions = emptyList())
        }
    }

    fun search() {
        val query = _uiState.value.query
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, error = null)
            searchRepository.searchVideos(query)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        results = result.videos.map { item ->
                            SearchResult(
                                bvid = item.bvid,
                                title = item.title,
                                cover = item.coverUrl,
                                uploader = item.uploader,
                                views = formatCount(item.views),
                                duration = item.duration
                            )
                        },
                        isSearching = false,
                        suggestions = emptyList()
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        error = e.message
                    )
                }
        }
    }

    companion object {
        fun formatCount(count: Long): String = when {
            count >= 10000 -> "${count / 10000}万"
            count >= 1000 -> "${count / 1000}千"
            else -> count.toString()
        }
    }
}
