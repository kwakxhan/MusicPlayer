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

    fun onPlayPauseClick() = launchWithErrorHandling("play/pause") {
        if (playbackState.value.isPlaying) {
            musicController.pause()
        } else {
            musicController.resume()
        }
    }

    fun onPreviousClick() = launchWithErrorHandling("previous") {
        musicController.previous()
    }

    fun onNextClick() = launchWithErrorHandling("next") {
        musicController.next()
    }

    fun onSeekTo(position: Long) = launchWithErrorHandling("seekTo $position") {
        musicController.seekTo(position)
    }

    fun onRepeatClick() = launchWithErrorHandling("repeat") {
        musicController.toggleRepeatMode()
    }

    fun onShuffleClick() = launchWithErrorHandling("shuffle") {
        musicController.toggleShuffle()
    }

    private inline fun launchWithErrorHandling(
        action: String,
        crossinline block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                Timber.e(e, "Error:: $action")
            }
        }
    }
}