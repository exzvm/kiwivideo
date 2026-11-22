package com.dylanxy.kiwivideo.data

import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class MovieCatalogJsonTest {

    @Test
    fun `decodes a movie list`() {
        val raw = """
            [
              {
                "id": "sintel",
                "title": "Sintel",
                "year": 2010,
                "durationMinutes": 15,
                "genres": ["动画", "奇幻"],
                "synopsis": "A lone girl searches for her dragon companion.",
                "posterUrl": "https://example.com/sintel.jpg",
                "videoUrl": "https://example.com/sintel.mp4"
              }
            ]
        """.trimIndent()

        val movies = MovieCatalogJson.decode(raw)

        assertEquals(1, movies.size)
        assertEquals("sintel", movies[0].id)
        assertEquals("Sintel", movies[0].title)
        assertEquals(listOf("动画", "奇幻"), movies[0].genres)
    }

    @Test
    fun `ignores unknown fields`() {
        val raw = """
            [
              {
                "id": "sintel",
                "title": "Sintel",
                "year": 2010,
                "durationMinutes": 15,
                "rating": 8.2
              }
            ]
        """.trimIndent()

        val movies = MovieCatalogJson.decode(raw)

        assertEquals(1, movies.size)
        assertEquals("Sintel", movies[0].title)
    }

    @Test
    fun `throws on malformed json`() {
        assertThrows(SerializationException::class.java) {
            MovieCatalogJson.decode("[{")
        }
    }
}
