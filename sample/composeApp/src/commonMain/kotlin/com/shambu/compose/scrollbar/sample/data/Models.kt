package com.shambu.compose.scrollbar.sample.data

data class AlbumModel(
    val title: String,
    val description: String,
    val songs: List<SongModel>,
    val imageUrl: String,
    val style: String,
)

data class SongModel(
    val title: String,
    val description: String,
    val imageUrl: String,
    val album: String
)