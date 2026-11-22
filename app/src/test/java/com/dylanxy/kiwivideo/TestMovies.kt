package com.dylanxy.kiwivideo

import com.dylanxy.kiwivideo.data.MovieRepository
import com.dylanxy.kiwivideo.data.model.Movie

fun sampleMovie(
    id: String = "sintel",
    title: String = "Sintel",
    genres: List<String> = listOf("动画", "奇幻"),
): Movie = Movie(
    id = id,
    title = title,
    year = 2010,
    durationMinutes = 15,
    genres = genres,
    synopsis = "A lone girl searches for her dragon companion.",
    posterUrl = "https://example.com/$id.jpg",
    videoUrl = "https://example.com/$id.mp4",
)

class FakeMovieRepository(
    private val movies: List<Movie> = emptyList(),
    var failure: Exception? = null,
) : MovieRepository {

    override suspend fun getMovies(): List<Movie> {
        failIfRequested()
        return movies
    }

    override suspend fun getMovie(id: String): Movie? {
        failIfRequested()
        return movies.firstOrNull { it.id == id }
    }

    private fun failIfRequested() {
        failure?.let { throw it }
    }
}
