package com.xhan.musicplayer.domain.repository

import com.xhan.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getAllTracks(): Flow<List<Track>>
    suspend fun getTrackById(id: Long): Track?
}