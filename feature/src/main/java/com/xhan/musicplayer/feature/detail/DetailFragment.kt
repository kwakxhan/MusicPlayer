package com.xhan.musicplayer.feature.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.xhan.musicplayer.core.util.formatDuration
import com.xhan.musicplayer.feature.R
import com.xhan.musicplayer.feature.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    private var isUserSeeking = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSeekBar()
        setupButtons()
        observePlaybackState()
    }

    private fun setupSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.currentTime.text = progress.toLong().formatDuration()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    viewModel.onSeekTo(it.progress.toLong())
                }
                isUserSeeking = false
            }
        })
    }

    private fun setupButtons() {
        binding.playPauseButton.setOnClickListener {
            viewModel.onPlayPauseClick()
        }

        binding.previousButton.setOnClickListener {
            viewModel.onPreviousClick()
        }

        binding.nextButton.setOnClickListener {
            viewModel.onNextClick()
        }
    }

    private fun observePlaybackState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playbackState.collect { state ->
                    updateUi(state)
                }
            }
        }
    }

    private fun updateUi(state: com.xhan.musicplayer.domain.model.PlaybackState) {
        val track = state.currentTrack

        if (track != null) {
            // 트랙 정보
            binding.title.text = track.title
            binding.artist.text = track.artist
            binding.album.text = track.album

            // 앨범 아트
            binding.albumArt.load(track.albumArtUri) {
                crossfade(enable = true)
                placeholder(R.drawable.ic_music_note)
                error(R.drawable.ic_music_note)
            }

            // SeekBar 업데이트
            if (!isUserSeeking) {
                binding.seekBar.max = state.duration.toInt()
                binding.seekBar.progress = state.position.toInt()
                binding.currentTime.text = state.position.formatDuration()
            }

            binding.totalTime.text = state.duration.formatDuration()
        }

        // 재생&일시정지 버튼 아이콘
        val playPauseIcon = if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        binding.playPauseButton.setImageResource(playPauseIcon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}