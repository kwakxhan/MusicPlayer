package com.xhan.musicplayer.domain.model

import android.net.Uri

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtUri: Uri?,
    val duration: Long,
    val contentUri: Uri
)