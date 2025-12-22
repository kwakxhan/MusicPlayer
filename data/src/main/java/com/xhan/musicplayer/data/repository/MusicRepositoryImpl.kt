package com.xhan.musicplayer.data.repository

import com.xhan.musicplayer.data.datasource.MediaStoreDataSource
import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource
) : MusicRepository {

    override fun getAllTracks(): Flow<List<Track>> = flow {
        val tracks = mediaStoreDataSource.getAllTracks()
        emit(tracks)
    }

    override suspend fun getTrackById(id: Long): Track? {
        return mediaStoreDataSource.getTrackById(id)
    }
}