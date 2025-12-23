package com.xhan.musicplayer.feature.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.xhan.musicplayer.core.util.formatDuration
import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.feature.R
import com.xhan.musicplayer.feature.databinding.ItemTrackBinding

class TrackAdapter(
    private val onTrackClick: (Track) -> Unit
) : ListAdapter<Track, TrackAdapter.TrackViewHolder>(DIFF_CALLBACK) {

    private var currentPlayingTrackId: Long? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder.create(parent, onTrackClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        val isPlaying = track.id == currentPlayingTrackId
        holder.bind(track, isPlaying)
    }

    /** 현재 재생 중인 트랙을 설정하고 UI를 업데이트 */
    fun setCurrentPlayingTrack(trackId: Long?) {
        val previousPlayingPosition = findTrackPosition(currentPlayingTrackId)
        val newPlayingPosition = findTrackPosition(trackId)

        currentPlayingTrackId = trackId

        updateItemIfValid(previousPlayingPosition)
        updateItemIfValid(newPlayingPosition)
    }

    private fun findTrackPosition(trackId: Long?): Int {
        return currentList.indexOfFirst { it.id == trackId }
    }

    private fun updateItemIfValid(position: Int) {
        if (position != INVALID_POSITION) notifyItemChanged(position)
    }

    class TrackViewHolder private constructor(
        private val binding: ItemTrackBinding,
        private val onTrackClick: (Track) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track, isPlaying: Boolean) {
            bindTrackInfo(track)
            bindAlbumArt(track)
            bindPlayingState(isPlaying)
            bindClickListener(track)
        }

        private fun bindTrackInfo(track: Track) {
            binding.title.text = track.title
            binding.artist.text = track.artist
            binding.duration.text = track.duration.formatDuration()
        }

        private fun bindAlbumArt(track: Track) {
            binding.albumArt.load(track.albumArtUri) {
                crossfade(enable = true)
                placeholder(R.drawable.ic_music_note)
                error(R.drawable.ic_music_note)
            }
        }

        private fun bindPlayingState(isPlaying: Boolean) {
            binding.nowPlayingIndicator.isVisible = isPlaying
        }

        private fun bindClickListener(track: Track) {
            binding.root.setOnClickListener {
                onTrackClick(track)
            }
        }

        companion object {
            fun create(parent: ViewGroup, onTrackClick: (Track) -> Unit): TrackViewHolder {
                val binding =
                    ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return TrackViewHolder(binding, onTrackClick)
            }
        }
    }

    companion object {
        private const val INVALID_POSITION = -1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem == newItem
            }
        }
    }
}