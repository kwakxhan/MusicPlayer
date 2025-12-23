package com.xhan.musicplayer.data.controller

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.xhan.musicplayer.data.service.PlayBackgroundService
import com.xhan.musicplayer.domain.controller.MusicController
import com.xhan.musicplayer.domain.model.PlaybackState
import com.xhan.musicplayer.domain.model.RepeatMode
import com.xhan.musicplayer.domain.model.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 음악 재생을 제어하는 Controller 구현체
 * 역할:
 * - PlayBackgroundService와 연결하여 MediaController를 관리
 * - UI에서 재생/일시정지/다음곡/이전곡 등의 제어 명령을 전달
 * - 현재 재생 상태를 Flow로 UI에 전달
 */
@Singleton
class MusicControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MusicController {

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main) // SupervisorJob: 자식 코루틴 중 하나가 실패해도 다른 코루틴들은 계속 실행

    private var mediaController: MediaController? = null
    private var currentTrack: Track? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    init {
        connectToService()
    }

    private fun connectToService() {
        scope.launch {
            runCatching {
                createMediaController()
            }.onSuccess { controller ->
                setupMediaController(controller)
            }.onFailure { error ->
                error.printStackTrace()
            }
        }
    }

    /** PlayBackgroundService와 연결 */
    private suspend fun createMediaController(): MediaController {
        val sessionToken =
            SessionToken(context, ComponentName(context, PlayBackgroundService::class.java))
        return MediaController.Builder(context, sessionToken)
            .buildAsync()
            .await()
    }

    private fun setupMediaController(controller: MediaController) {
        mediaController = controller
        controller.addListener(playerListener)
        updatePlaybackState()
    }

    /** Player의 상태 변경을 감지하는 리스너 */
    private val playerListener = object : Player.Listener {
        /** 재생 & 일시 정지 상태 변경 */
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlaybackState()
        }

        /** 트랙 전환 */
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updatePlaybackState()
        }

        /** 버퍼링, 준비 완료 등 등 */
        override fun onPlaybackStateChanged(playbackState: Int) {
            updatePlaybackState()
        }
    }

    /** 현재 재생 상태를 UI에 전달 */
    private fun updatePlaybackState() {
        val controller = mediaController ?: return
        _playbackState.value = PlaybackState(
            currentTrack = currentTrack,
            isPlaying = controller.isPlaying,
            position = controller.currentPosition,
            duration = controller.duration.coerceAtLeast(0L),
            repeatMode = controller.repeatMode.toDomainRepeatMode(),
            shuffleEnabled = controller.shuffleModeEnabled
        )
    }

    private fun Int.toDomainRepeatMode(): RepeatMode = when (this) {
        Player.REPEAT_MODE_OFF -> RepeatMode.OFF
        Player.REPEAT_MODE_ONE -> RepeatMode.ONE
        Player.REPEAT_MODE_ALL -> RepeatMode.ALL
        else -> RepeatMode.OFF
    }

    override suspend fun play(track: Track) {
        val controller = mediaController ?: return
        currentTrack = track
        val mediaItem = track.toMediaItem()
        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()
    }

    override suspend fun pause() {
        mediaController?.pause()
    }

    override suspend fun resume() {
        mediaController?.play()
    }

    override suspend fun next() {
        mediaController?.seekToNext()
    }

    override suspend fun previous() {
        mediaController?.seekToPrevious()
    }

    override suspend fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    private fun Track.toMediaItem(): MediaItem = MediaItem.Builder()
        .setUri(contentUri)
        .setMediaId(id.toString())
        .build()
}