package com.xhan.musicplayer.feature.view

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.xhan.musicplayer.domain.model.Track
import com.xhan.musicplayer.feature.R
import com.xhan.musicplayer.feature.detail.DetailViewModel

@Composable
fun MiniPlayer(
    viewModel: DetailViewModel,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // lifecycleScope.launch + repeatOnLifecycle과 동일
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()

    // 트랙이 있을 때만 표시
    playbackState.currentTrack?.let { track ->
        MiniPlayerContent(
            track = track,
            isPlaying = playbackState.isPlaying,
            onExpandClick = onExpandClick,
            onPlayPauseClick = { viewModel.onPlayPauseClick() },
            modifier = modifier
        )
    }
}

@Composable
private fun MiniPlayerContent(
    track: Track,
    isPlaying: Boolean,
    onExpandClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onExpandClick,
        modifier = modifier.fillMaxWidth(),
        color = colorResource(id = com.xhan.musicplayer.core.R.color.player_background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 앨범 아트
            AsyncImage(
                model = track.albumArtUri,
                contentDescription = "Album Art",
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_music_note),
                error = painterResource(R.drawable.ic_music_note)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 트랙 정보
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = com.xhan.musicplayer.core.R.color.player_text),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
                Text(
                    text = track.artist,
                    fontSize = 12.sp,
                    color = colorResource(id = com.xhan.musicplayer.core.R.color.player_text_secondary),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            }

            // 재생/정지 버튼
            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(24.dp),
                    tint = colorResource(id = com.xhan.musicplayer.core.R.color.icon_tint)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MiniPlayerPreview() {
    MaterialTheme {
        MiniPlayerContent(
            track = Track(
                id = 1,
                title = "Sample Song Title That Is Very Long Long Long",
                artist = "Sample Artist Name",
                album = "Sample Album",
                albumArtUri = android.net.Uri.EMPTY,
                duration = 180000,
                contentUri = android.net.Uri.EMPTY
            ),
            isPlaying = false,
            onExpandClick = {},
            onPlayPauseClick = {}
        )
    }
}