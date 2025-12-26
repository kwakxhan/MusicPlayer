package com.xhan.musicplayer.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xhan.musicplayer.core.util.Constants.STOP_TIMEOUT_MILLIS
import com.xhan.musicplayer.domain.controller.MusicController
import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.domain.usecase.GetTracksUseCase
import com.xhan.musicplayer.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getTracksUseCase: GetTracksUseCase,
    private val musicController: MusicController
) : ViewModel() {

    val tracks: StateFlow<List<Track>> = getTracksUseCase()
        .map { result ->
            when (result) {
                is Result.Success -> result.data
                is Result.Error -> {
                    handleError(result.exception)
                    emptyList()
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    val currentPlayingTrack: StateFlow<Track?> = musicController.playbackState
        .map { it.currentTrack }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = null
        )

    private val _uiEvent = Channel<Event>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onTrackClick(track: Track) {
        viewModelScope.launch {
            try {
                val allTracks = tracks.value
                val index = allTracks.indexOfFirst { it.id == track.id }
                if (index >= 0) musicController.playAll(allTracks, index)
            } catch (e: Exception) {
                Timber.e(e, "Error track:: ${track.title}")
            }
        }
    }

    private fun handleError(exception: Exception) {
        viewModelScope.launch {
            val event = when (exception) {
                is SecurityException -> Event.ShowPermissionErrorDialog
                is IllegalArgumentException -> Event.ShowInvalidDataErrorDialog
                else -> Event.ShowUnexpectedErrorDialog
            }
            _uiEvent.send(event)
        }
    }

    sealed class Event {
        data object ShowPermissionErrorDialog : Event()
        data object ShowInvalidDataErrorDialog : Event()
        data object ShowUnexpectedErrorDialog : Event()
    }
}