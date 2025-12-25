package com.xhan.musicplayer.data.repository

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.xhan.musicplayer.data.datasource.MediaStoreDataSource
import com.xhan.musicplayer.data.datasource.TrackPagingSource
import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource,
    private val contentResolver: ContentResolver
) : MusicRepository {

    override fun getAllTracks(): Flow<List<Track>> = callbackFlow {
        val observer = createMediaStoreObserver()
        registerObserver(observer)
        emitTracks()
        awaitClose { unregisterObserver(observer) }
    }

    override suspend fun getTrackById(id: Long): Track? {
        return mediaStoreDataSource.getTrackById(id)
    }

    override fun getPagedTracks(): Flow<PagingData<Track>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = { TrackPagingSource(mediaStoreDataSource) }
        ).flow
    }

    /** MediaStore 변경을 감지하는 ContentObserver 생성 */
    private fun ProducerScope<List<Track>>.createMediaStoreObserver(): ContentObserver {
        return object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                launch(Dispatchers.IO) { emitTracks() }
            }
        }
    }

    /** ContentObserver를 MediaStore에 등록 */
    private fun registerObserver(observer: ContentObserver) {
        contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            NOTIFY_FOR_DESCENDANTS,
            observer
        )
    }

    /** 음악 목록을 조회 후 Flow로 방출 */
    private suspend fun ProducerScope<List<Track>>.emitTracks() {
        val tracks = mediaStoreDataSource.getAllTracks()
        send(tracks)
    }

    /** ContentObserver 등록 해제 */
    private fun unregisterObserver(observer: ContentObserver) {
        contentResolver.unregisterContentObserver(observer)
    }

    companion object {
        /** 하위 URI 변경 시에도 감지 */
        private const val NOTIFY_FOR_DESCENDANTS = true

        /** 페이지네이션 크기 */
        private const val PAGE_SIZE = 20
    }
}