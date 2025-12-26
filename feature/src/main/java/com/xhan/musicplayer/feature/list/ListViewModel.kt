package com.xhan.musicplayer.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xhan.musicplayer.domain.controller.MusicController
import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.domain.usecase.GetTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getTracksUseCase: GetTracksUseCase,
    private val musicController: MusicController
) : ViewModel() {

    val tracks: StateFlow<List<Track>> = getTracksUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val currentPlayingTrack: StateFlow<Track?> = musicController.playbackState
        .map { it.currentTrack }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun onTrackClick(track: Track) {
        viewModelScope.launch {
            try {
                val allTracks = tracks.value
                val index = allTracks.indexOfFirst { it.id == track.id }
                if (index >= 0) {
                    musicController.playAll(allTracks, index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}