package com.xhan.musicplayer.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xhan.musicplayer.domain.controller.MusicController
import com.xhan.musicplayer.domain.model.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val musicController: MusicController
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = musicController.playbackState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
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
                e.printStackTrace()
            }
        }
    }

    fun onPreviousClick() {
        viewModelScope.launch {
            try {
                musicController.previous()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onNextClick() {
        viewModelScope.launch {
            try {
                musicController.next()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onSeekTo(position: Long) {
        viewModelScope.launch {
            try {
                musicController.seekTo(position)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}