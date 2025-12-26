package com.xhan.musicplayer.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xhan.musicplayer.core.util.Constants.STOP_TIMEOUT_MILLIS
import com.xhan.musicplayer.domain.controller.MusicController
import com.xhan.musicplayer.domain.model.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val musicController: MusicController
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = musicController.playbackState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = PlaybackState()
        )

    fun onPlayPauseClick() {
        viewModelScope.launch {
            try {
                if (playbackState.value.isPlaying) {
                    musicController.pause()
                } else {
                    musicController.resume()
                }
            } catch (e: Exception) {
                handleError("play/pause", e)
            }
        }
    }

    fun onPreviousClick() {
        viewModelScope.launch {
            try {
                musicController.previous()
            } catch (e: Exception) {
                handleError("previous", e)
            }
        }
    }

    fun onNextClick() {
        viewModelScope.launch {
            try {
                musicController.next()
            } catch (e: Exception) {
                handleError("next", e)
            }
        }
    }

    fun onSeekTo(position: Long) {
        viewModelScope.launch {
            try {
                musicController.seekTo(position)
            } catch (e: Exception) {
                handleError("seekTo $position", e)
            }
        }
    }

    fun onRepeatClick() {
        viewModelScope.launch {
            try {
                musicController.toggleRepeatMode()
            } catch (e: Exception) {
                handleError("repeat", e)
            }
        }
    }

    fun onShuffleClick() {
        viewModelScope.launch {
            try {
                musicController.toggleShuffle()
            } catch (e: Exception) {
                handleError("shuffle", e)
            }
        }
    }

    private fun handleError(action: String, exception: Exception) {
        Timber.e(exception, "Error:: $action")
    }
}