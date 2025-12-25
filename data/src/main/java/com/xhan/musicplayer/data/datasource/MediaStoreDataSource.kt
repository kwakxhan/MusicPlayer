package com.xhan.musicplayer.data.datasource

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import com.xhan.musicplayer.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaStoreDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    suspend fun getAllTracks(): List<Track> = withContext(Dispatchers.IO) {
        queryTracks(
            selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0", // "IS_MUSIC != 0" → 음악 파일만 찾기
            selectionArgs = null,
            sortOrder = "${MediaStore.Audio.Media.TITLE} ASC" // 제목 오름차순 정렬
        )
    }

    suspend fun getTrackById(trackId: Long): Track? = withContext(Dispatchers.IO) {
        queryTracks(
            selection = "${MediaStore.Audio.Media._ID} = ?", // "_ID = ?" → 특정 ID의 음악 찾기
            selectionArgs = arrayOf(trackId.toString()), // 탐색할 ID
            sortOrder = null
        ).firstOrNull()
    }

    suspend fun getPagedTracks(offset: Int, limit: Int): List<Track> = withContext(Dispatchers.IO) {
        queryTracks(
            selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            selectionArgs = null,
            sortOrder = "${MediaStore.Audio.Media.TITLE} ASC LIMIT $limit OFFSET $offset"
        )
    }

    /** MediaStore에서 조건에 맞는 음악 파일 검색 */
    private fun queryTracks(
        selection: String,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): List<Track> {
        return contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // 외부 저장소의 음악 파일 데이터베이스에서 탐색
            PROJECTION, // 탐색할 데이터(ID, 제목, 아티스트, 앨범, 길이)
            selection, // 비교할 필드
            selectionArgs, // 검색할 데이터
            sortOrder // 정렬
        )?.use { cursor -> // 검색 결과의 각 행을 가리키는 포인터
            cursor.toTrackList() // Cursor의 모든 행을 Track 리스트로 변환
        } ?: emptyList() // 쿼리 결과가 null이면 빈 리스트 반환
    }

    /** Cursor의 모든 행을 Track 리스트로 변환 */
    private fun Cursor.toTrackList(): List<Track> {
        val tracks = mutableListOf<Track>()
        while (moveToNext()) {
            tracks.add(toTrack())
        }
        return tracks
    }

    /** Cursor의 현재 행을 Track 객체로 변환 */
    private fun Cursor.toTrack(): Track {
        val id =
            getLong(getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
        val title =
            getString(getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)) ?: DEFAULT_TITLE
        val artist =
            getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)) ?: DEFAULT_ARTIST
        val album =
            getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)) ?: DEFAULT_ALBUM
        val albumId =
            getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
        val duration =
            getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

        return Track(
            id = id,
            title = title,
            artist = artist,
            album = album,
            albumArtUri = getAlbumArtUri(albumId),
            duration = duration,
            contentUri = getContentUri(id)
        )
    }

    private fun getContentUri(trackId: Long): Uri {
        return ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        )
    }

    private fun getAlbumArtUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(
            ALBUM_ART_URI,
            albumId
        )
    }

    companion object {
        private val PROJECTION = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )

        private val ALBUM_ART_URI = "content://media/external/audio/albumart".toUri()
        private const val DEFAULT_TITLE = "Unknown Title"
        private const val DEFAULT_ARTIST = "Unknown Artist"
        private const val DEFAULT_ALBUM = "Unknown Album"
    }
}
