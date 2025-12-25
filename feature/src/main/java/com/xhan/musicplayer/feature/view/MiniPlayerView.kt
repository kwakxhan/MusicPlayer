package com.xhan.musicplayer.feature.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.xhan.musicplayer.feature.R
import com.xhan.musicplayer.feature.databinding.LayoutMiniPlayerBinding
import com.xhan.musicplayer.feature.detail.DetailViewModel
import kotlinx.coroutines.launch

class MiniPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutMiniPlayerBinding =
        LayoutMiniPlayerBinding.inflate(LayoutInflater.from(context), this)

    private var onExpandClickListener: (() -> Unit)? = null
    private var onPlayPauseClickListener: (() -> Unit)? = null

    init {
        setOnClickListener {
            onExpandClickListener?.invoke()
        }

        binding.miniPlayPauseButton.setOnClickListener {
            onPlayPauseClickListener?.invoke()
        }
    }

    fun bind(viewModel: DetailViewModel, lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playbackState.collect { playbackState ->
                    val track = playbackState.currentTrack

                    // Mini Player 표시 여부 (트랙이 있으면 표시)
                    isVisible = track != null

                    track?.let {
                        // 앨범 아트
                        binding.miniAlbumArt.load(it.albumArtUri) {
                            crossfade(true)
                            placeholder(R.drawable.ic_music_note)
                            error(R.drawable.ic_music_note)
                        }

                        // 트랙 정보
                        binding.miniTitle.text = it.title
                        binding.miniArtist.text = it.artist

                        // 재생/정지 아이콘
                        val iconRes =
                            if (playbackState.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                        binding.miniPlayPauseButton.setImageResource(iconRes)
                    }
                }
            }
        }
    }

    fun setOnExpandClickListener(listener: () -> Unit) {
        onExpandClickListener = listener
    }

    fun setOnPlayPauseClickListener(listener: () -> Unit) {
        onPlayPauseClickListener = listener
    }
}