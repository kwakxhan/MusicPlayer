package com.xhan.musicplayer.core.util

import java.util.Locale

/**
 * 밀리초를 "분:초" 형식의 문자열로 변환
 * @return "분:초" 형식의 문자열 (예: "3:45", "12:03")
 */
fun Long.formatDuration(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}