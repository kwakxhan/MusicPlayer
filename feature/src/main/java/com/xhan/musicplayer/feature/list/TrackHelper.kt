package com.xhan.musicplayer.feature.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.xhan.musicplayer.core.util.formatDuration
import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.feature.R
import com.xhan.musicplayer.feature.databinding.ItemTrackBinding
import com.xhan.musicplayer.feature.util.BasePagingAdapter
import com.xhan.musicplayer.feature.util.ItemHelper
import com.xhan.musicplayer.feature.util.OnItemClick

class TrackHelper(
    private val onItem: OnItemClick<Track, ItemTrackBinding>
) : ItemHelper<Track, ItemTrackBinding> {

    private var currentPlayingTrackId: Long? = null

    override fun getDiffCallback(): DiffUtil.ItemCallback<Track> {
        return BasePagingAdapter.createDiffCallbackById { it.id }
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTrackBinding {
        return ItemTrackBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemTrackBinding, item: Track, position: Int) {
        bindTrackInfo(binding, item)
        bindAlbumArt(binding, item)
        bindPlayingState(binding, item)
    }

    override fun onItemClick(item: Track, binding: ItemTrackBinding, position: Int): Boolean {
        return onItem.onClick(item, binding, position)
    }

    private fun bindTrackInfo(binding: ItemTrackBinding, track: Track) {
        binding.title.text = track.title
        binding.artist.text = track.artist
        binding.duration.text = track.duration.formatDuration()
    }

    private fun bindAlbumArt(binding: ItemTrackBinding, track: Track) {
        binding.albumArt.load(track.albumArtUri) {
            crossfade(enable = true)
            placeholder(R.drawable.ic_music_note)
            error(R.drawable.ic_music_note)
        }
    }

    private fun bindPlayingState(binding: ItemTrackBinding, track: Track) {
        val isPlaying = track.id == currentPlayingTrackId
        binding.nowPlayingIndicator.isVisible = isPlaying
    }

    /** 현재 재생 중인 트랙 설정 및 UI 업데이트 */
    fun setCurrentPlayingTrack(
        trackId: Long?,
        adapter: BasePagingAdapter<Track, ItemTrackBinding>
    ) {
        val previousId = currentPlayingTrackId
        currentPlayingTrackId = trackId

        // 이전 재생 중이던 아이템과 새로운 재생 아이템의 위치를 찾아서 업데이트
        val snapshot = adapter.snapshot()
        val previousPosition = snapshot.items.indexOfFirst { it.id == previousId }
        val newPosition = snapshot.items.indexOfFirst { it.id == trackId }

        if (previousPosition != -1) adapter.notifyItemChanged(previousPosition)
        if (newPosition != -1) adapter.notifyItemChanged(newPosition)
    }
}