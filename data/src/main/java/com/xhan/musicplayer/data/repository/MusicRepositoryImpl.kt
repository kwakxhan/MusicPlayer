package com.xhan.musicplayer.data.repository

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.xhan.musicplayer.data.datasource.MediaStoreDataSource
import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.domain.repository.MusicRepository
import com.xhan.musicplayer.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource,
    private val contentResolver: ContentResolver
) : MusicRepository {

    override fun getAllTracks(): Flow<Result<List<Track>>> = callbackFlow {
        val observer = createMediaStoreObserver()
        registerObserver(observer)
        emitTracks()
        awaitClose { unregisterObserver(observer) }
    }

    override suspend fun getTrackById(id: Long): Result<Track?> {
        return mediaStoreDataSource.getTrackById(id)
    }

    /** MediaStore 변경을 감지하는 ContentObserver 생성 */
    private fun ProducerScope<Result<List<Track>>>.createMediaStoreObserver(): ContentObserver {
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
    private suspend fun ProducerScope<Result<List<Track>>>.emitTracks() {
        val result = mediaStoreDataSource.getAllTracks()
        send(result)
    }

    /** ContentObserver 등록 해제 */
    private fun unregisterObserver(observer: ContentObserver) {
        contentResolver.unregisterContentObserver(observer)
    }

    companion object {
        /** 하위 URI 변경 시에도 감지 */
        private const val NOTIFY_FOR_DESCENDANTS = true
    }
}