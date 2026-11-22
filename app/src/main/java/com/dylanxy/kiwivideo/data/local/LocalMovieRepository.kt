package com.dylanxy.kiwivideo.data.local

import com.dylanxy.kiwivideo.data.MovieRepository
import com.dylanxy.kiwivideo.data.model.Movie

class LocalMovieRepository(
    private val movies: List<Movie>,
) : MovieRepository {

    override suspend fun getMovies(): List<Movie> = movies

    override suspend fun getMovie(id: String): Movie? = movies.firstOrNull { it.id == id }
}
