package com.xhan.musicplayer.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xhan.musicplayer.domain.controller.MusicController
import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.domain.usecase.GetTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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

    val pagedTracks: Flow<PagingData<Track>> = getTracksUseCase.getPaged()
        .cachedIn(viewModelScope)

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
                musicController.play(track)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}