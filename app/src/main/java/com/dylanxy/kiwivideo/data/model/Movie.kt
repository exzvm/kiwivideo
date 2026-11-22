package com.dylanxy.kiwivideo.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: String,
    val title: String,
    val year: Int,
    val durationMinutes: Int,
    val genres: List<String> = emptyList(),
    val synopsis: String = "",
    val posterUrl: String = "",
    val videoUrl: String = "",
)
