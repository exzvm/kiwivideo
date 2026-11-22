package com.dylanxy.kiwivideo.data

import com.dylanxy.kiwivideo.data.model.Movie

interface MovieRepository {
    suspend fun getMovies(): List<Movie>

    suspend fun getMovie(id: String): Movie?
}
