package com.xhan.musicplayer.domain.usecase

import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetTrackByIdUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(id: Long): Track? {
        return musicRepository.getTrackById(id)
    }
}