package com.xhan.musicplayer.domain.usecase

import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(): Flow<List<Track>> {
        return musicRepository.getAllTracks()
    }

    suspend fun getTrackById(id: Long): Track? {
        return musicRepository.getTrackById(id)
    }
}