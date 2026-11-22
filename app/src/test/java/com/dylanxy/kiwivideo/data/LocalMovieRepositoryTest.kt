package com.dylanxy.kiwivideo.data

import com.dylanxy.kiwivideo.data.local.LocalMovieRepository
import com.dylanxy.kiwivideo.sampleMovie
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LocalMovieRepositoryTest {

    private val repository = LocalMovieRepository(
        listOf(
            sampleMovie(id = "sintel", title = "Sintel"),
            sampleMovie(id = "bunny", title = "Big Buck Bunny"),
        ),
    )

    @Test
    fun `returns every movie`() = runTest {
        assertEquals(2, repository.getMovies().size)
    }

    @Test
    fun `finds a movie by id`() = runTest {
        assertEquals("Big Buck Bunny", repository.getMovie("bunny")?.title)
    }

    @Test
    fun `returns null for an unknown id`() = runTest {
        assertNull(repository.getMovie("missing"))
    }
}
