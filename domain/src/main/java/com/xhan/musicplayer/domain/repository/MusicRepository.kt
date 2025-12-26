package com.xhan.musicplayer.domain.repository

import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getAllTracks(): Flow<Result<List<Track>>>
    suspend fun getTrackById(id: Long): Result<Track?>
}