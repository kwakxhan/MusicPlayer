package com.xhan.musicplayer.domain.repository

import androidx.paging.PagingData
import com.xhan.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getAllTracks(): Flow<List<Track>>
    fun getPagedTracks(): Flow<PagingData<Track>>
    suspend fun getTrackById(id: Long): Track?
}