package com.xhan.musicplayer.data.service

import android.app.PendingIntent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint

/**
 * 음악 재생을 담당하는 백그라운드 서비스
 * 동작 원리:
 * 1. ExoPlayer가 실제 음악 파일을 재생
 * 2. MediaSession이 재생 상태를 시스템에 알림
 * 3. 시스템이 자동으로 Notification을 생성
 * 4. 사용자가 Notification, 블루투스 등으로 제어 가능
 */
@AndroidEntryPoint
class PlayBackgroundService : MediaSessionService() {

    /** MediaSession: 재생 정보를 외부에 공유하는 창구 */
    private var mediaSession: MediaSession? = null

    /** ExoPlayer: 실제로 음악을 재생하는 플레이어 */
    private var player: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()
        player = buildPlayer()
        mediaSession = buildMediaSession(player)
    }

    /** 외부 클라이언트(앱 UI, Notification, Bluetooth, Android Auto)가 이 서비스에 연결을 요청할 때 호출 */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    private fun buildPlayer(): ExoPlayer {
        return ExoPlayer.Builder(this)
            .setAudioAttributes(buildAudioAttributes(), HANDLE_AUDIO_FOCUS) // 전화 오면 음악 자동 일시 정지
            .setHandleAudioBecomingNoisy(true) // 헤드폰 뽑으면 음악 자동 정지
            .build()
    }

    private fun buildAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC) // 음악 타입
            .setUsage(C.USAGE_MEDIA)
            .build()
    }

    private fun buildMediaSession(player: ExoPlayer?): MediaSession? {
        return player?.let {
            MediaSession.Builder(this, it)
                .setSessionActivity(buildSessionActivity())
                .build()
        }
    }

    /** Notification 클릭 시 앱 실행 */
    private fun buildSessionActivity(): PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        return PendingIntent.getActivity(this, REQUEST_CODE, intent, PENDING_INTENT_FLAGS)
    }

    override fun onDestroy() {
        releaseResources()
        super.onDestroy()
    }

    private fun releaseResources() {
        mediaSession?.run {
            player.release() // ExoPlayer 정리
            release() // MediaSession 정리
        }
        mediaSession = null
        player = null
    }

    companion object {
        private const val HANDLE_AUDIO_FOCUS = true
        private const val REQUEST_CODE = 0

        /**
         * - IMMUTABLE: 보안을 위해 수정 불가능하게 설정
         * - UPDATE_CURRENT: 기존 PendingIntent가 있으면 업데이트
         */
        private const val PENDING_INTENT_FLAGS =
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    }
}