package com.xhan.musicplayer.domain.usecase

import androidx.paging.PagingData
import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    /**
     * 모든 트랙 목록을 Flow로 반환
     */
    operator fun invoke(): Flow<List<Track>> {
        return musicRepository.getAllTracks()
    }

    /**
     * 페이징된 트랙 목록을 Flow로 반환
     */
    fun getPaged(): Flow<PagingData<Track>> {
        return musicRepository.getPagedTracks()
    }

    /**
     * ID로 특정 트랙 조회
     */
    suspend fun getTrackById(id: Long): Track? {
        return musicRepository.getTrackById(id)
    }
}