package com.dylanxy.kiwivideo.data

import com.dylanxy.kiwivideo.data.model.Movie
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

object MovieCatalogJson {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun decode(raw: String): List<Movie> =
        json.decodeFromString(ListSerializer(Movie.serializer()), raw)
}
