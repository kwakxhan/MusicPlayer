package com.xhan.musicplayer.feature.util

import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import coil.load
import com.xhan.musicplayer.core.util.formatDuration
import com.xhan.musicplayer.domain.model.RepeatMode
import com.xhan.musicplayer.feature.R
import com.xhan.musicplayer.feature.view.MarqueeTextView

/** 트랙 텍스트 바인딩 */
@BindingAdapter("trackText", "trackId", requireAll = false)
fun MarqueeTextView.bindTrackText(text: String?, trackId: Long?) {
    setTrackText(text, trackId)
}

/** 앨범 아트 이미지 로딩 */
@BindingAdapter("albumArtUri")
fun ImageView.loadAlbumArt(uri: Uri?) {
    load(uri) {
        crossfade(enable = true)
        placeholder(R.drawable.ic_music_note)
        error(R.drawable.ic_music_note)
    }
}

/** 재생 시간 텍스트 포맷팅 */
@BindingAdapter("durationText")
fun TextView.setDurationText(millis: Long?) {
    text = millis?.formatDuration() ?: "0:00"
}

/** 재생/일시정지 버튼 아이콘 */
@BindingAdapter("playPauseIcon")
fun ImageButton.setPlayPauseIcon(isPlaying: Boolean) {
    val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
    setImageResource(iconRes)
}

/** RepeatMode 버튼 아이콘 */
@BindingAdapter("repeatModeIcon")
fun ImageButton.setRepeatModeIcon(repeatMode: RepeatMode?) {
    val iconRes = when (repeatMode) {
        RepeatMode.OFF -> R.drawable.ic_repeat_off
        RepeatMode.ALL -> R.drawable.ic_repeat_all
        RepeatMode.ONE -> R.drawable.ic_repeat_one
        null -> R.drawable.ic_repeat_off
    }
    setImageResource(iconRes)
}

/** Shuffle 버튼 아이콘 */
@BindingAdapter("shuffleIcon")
fun ImageButton.setShuffleIcon(enabled: Boolean) {
    val iconRes = if (enabled) R.drawable.ic_shuffle_on else R.drawable.ic_shuffle_off
    setImageResource(iconRes)
}

/** View Visibility 바인딩 */
@BindingAdapter("isVisible")
fun View.setVisible(visible: Boolean) {
    isVisible = visible
}