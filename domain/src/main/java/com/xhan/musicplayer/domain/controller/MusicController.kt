package com.xhan.musicplayer.domain.controller

import com.xhan.musicplayer.domain.model.PlaybackState
import com.xhan.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface MusicController {
    val playbackState: StateFlow<PlaybackState>
    
    suspend fun play(track: Track)
    suspend fun playAll(tracks: List<Track>, startIndex: Int = 0)
    suspend fun pause()
    suspend fun resume()
    suspend fun next()
    suspend fun previous()
    suspend fun seekTo(position: Long)
    suspend fun toggleRepeatMode()
    suspend fun toggleShuffle()
}
