package com.xhan.musicplayer.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xhan.musicplayer.domain.controller.MusicController
import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.domain.usecase.GetTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getTracksUseCase: GetTracksUseCase,
    private val musicController: MusicController
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val uiState: StateFlow<ListUiState> = combine(
        getTracksUseCase(),
        musicController.playbackState,
        isLoading
    ) { tracks, playbackState, isLoading ->
        ListUiState(
            tracks = tracks,
            currentPlayingTrack = playbackState.currentTrack,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListUiState()
    )

    fun onTrackClick(track: Track) {
        viewModelScope.launch {
            try {
                musicController.play(track)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class ListUiState(
    val tracks: List<Track> = emptyList(),
    val currentPlayingTrack: Track? = null,
    val isLoading: Boolean = false
)