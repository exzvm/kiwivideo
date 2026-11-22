package com.dylanxy.kiwivideo.ui.home

import com.dylanxy.kiwivideo.FakeMovieRepository
import com.dylanxy.kiwivideo.MainDispatcherRule
import com.dylanxy.kiwivideo.sampleMovie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `emits success with the movie list`() = runTest(mainDispatcherRule.dispatcher.scheduler) {
        val movies = listOf(sampleMovie())

        val viewModel = HomeViewModel(FakeMovieRepository(movies))

        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()
        assertEquals(
            HomeUiState.Success(movies, genres = listOf("动画", "奇幻")),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `emits error when loading fails`() = runTest(mainDispatcherRule.dispatcher.scheduler) {
        val viewModel = HomeViewModel(
            FakeMovieRepository(failure = IllegalStateException("boom")),
        )

        advanceUntilIdle()

        assertEquals(HomeUiState.Error("boom"), viewModel.uiState.value)
    }

    @Test
    fun `filters by title ignoring case when the query changes`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val sintel = sampleMovie(id = "sintel", title = "Sintel")
            val bunny = sampleMovie(id = "bunny", title = "Big Buck Bunny")
            val viewModel = HomeViewModel(FakeMovieRepository(listOf(sintel, bunny)))
            advanceUntilIdle()

            viewModel.onQueryChange("BUNNY")

            assertEquals(
                HomeUiState.Success(
                    listOf(bunny),
                    query = "BUNNY",
                    genres = listOf("动画", "奇幻"),
                ),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `blank query shows every movie`() = runTest(mainDispatcherRule.dispatcher.scheduler) {
        val movies = listOf(
            sampleMovie(id = "sintel", title = "Sintel"),
            sampleMovie(id = "bunny", title = "Big Buck Bunny"),
        )
        val viewModel = HomeViewModel(FakeMovieRepository(movies))
        advanceUntilIdle()

        viewModel.onQueryChange("   ")

        assertEquals(
            HomeUiState.Success(movies, query = "   ", genres = listOf("动画", "奇幻")),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `no matches yields an empty list`() = runTest(mainDispatcherRule.dispatcher.scheduler) {
        val viewModel = HomeViewModel(
            FakeMovieRepository(listOf(sampleMovie(title = "Sintel"))),
        )
        advanceUntilIdle()

        viewModel.onQueryChange("zzz")

        assertEquals(
            HomeUiState.Success(
                emptyList(),
                query = "zzz",
                genres = listOf("动画", "奇幻"),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `retry keeps the current query`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val sintel = sampleMovie(id = "sintel", title = "Sintel")
            val bunny = sampleMovie(id = "bunny", title = "Big Buck Bunny")
            val repository = FakeMovieRepository(
                movies = listOf(sintel, bunny),
                failure = IllegalStateException("offline"),
            )
            val viewModel = HomeViewModel(repository)
            advanceUntilIdle()
            viewModel.onQueryChange("bunny")

            repository.failure = null
            viewModel.loadMovies()

            assertEquals(HomeUiState.Loading, viewModel.uiState.value)
            advanceUntilIdle()
            assertEquals(
                HomeUiState.Success(
                    listOf(bunny),
                    query = "bunny",
                    genres = listOf("动画", "奇幻"),
                ),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `filters by the selected genre`() = runTest(mainDispatcherRule.dispatcher.scheduler) {
        val sintel = sampleMovie(id = "sintel", title = "Sintel", genres = listOf("动画", "奇幻"))
        val tears = sampleMovie(id = "tears", title = "Tears of Steel", genres = listOf("科幻", "动作"))
        val viewModel = HomeViewModel(FakeMovieRepository(listOf(sintel, tears)))
        advanceUntilIdle()

        viewModel.onGenreSelected("科幻")

        val state = viewModel.uiState.value as HomeUiState.Success
        assertEquals(listOf(tears), state.movies)
        assertEquals("科幻", state.selectedGenre)
    }

    @Test
    fun `exposes distinct genres from the full catalog`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val movies = listOf(
                sampleMovie(id = "sintel", genres = listOf("动画", "奇幻")),
                sampleMovie(id = "tears", genres = listOf("科幻", "动画")),
            )
            val viewModel = HomeViewModel(FakeMovieRepository(movies))
            advanceUntilIdle()

            val state = viewModel.uiState.value as HomeUiState.Success

            assertEquals(listOf("动画", "奇幻", "科幻"), state.genres)
        }

    @Test
    fun `clearing the genre shows every movie`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val sintel = sampleMovie(id = "sintel", genres = listOf("动画"))
            val tears = sampleMovie(id = "tears", genres = listOf("科幻"))
            val viewModel = HomeViewModel(FakeMovieRepository(listOf(sintel, tears)))
            advanceUntilIdle()
            viewModel.onGenreSelected("科幻")

            viewModel.onGenreSelected(null)

            val state = viewModel.uiState.value as HomeUiState.Success
            assertEquals(listOf(sintel, tears), state.movies)
            assertNull(state.selectedGenre)
        }

    @Test
    fun `combines query and genre filters`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val sintel = sampleMovie(
                id = "sintel",
                title = "Sintel",
                genres = listOf("动画", "奇幻"),
            )
            val bunny = sampleMovie(
                id = "bunny",
                title = "Big Buck Bunny",
                genres = listOf("动画", "喜剧"),
            )
            val tears = sampleMovie(
                id = "tears",
                title = "Tears of Steel",
                genres = listOf("科幻", "动作"),
            )
            val viewModel = HomeViewModel(FakeMovieRepository(listOf(sintel, bunny, tears)))
            advanceUntilIdle()
            viewModel.onGenreSelected("动画")
            assertEquals(
                listOf(sintel, bunny),
                (viewModel.uiState.value as HomeUiState.Success).movies,
            )

            viewModel.onQueryChange("e")

            val state = viewModel.uiState.value as HomeUiState.Success
            assertEquals(listOf(sintel), state.movies)
            assertEquals("e", state.query)
            assertEquals("动画", state.selectedGenre)
        }

    @Test
    fun `keeps every genre available after selecting one`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val movies = listOf(
                sampleMovie(id = "sintel", genres = listOf("动画", "奇幻")),
                sampleMovie(id = "tears", genres = listOf("科幻", "动画")),
            )
            val viewModel = HomeViewModel(FakeMovieRepository(movies))
            advanceUntilIdle()

            viewModel.onGenreSelected("动画")

            val state = viewModel.uiState.value as HomeUiState.Success
            assertEquals(listOf("动画", "奇幻", "科幻"), state.genres)
        }

    @Test
    fun `clear filters resets query and genre`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val sintel = sampleMovie(id = "sintel", title = "Sintel", genres = listOf("动画"))
            val tears = sampleMovie(id = "tears", title = "Tears of Steel", genres = listOf("科幻"))
            val viewModel = HomeViewModel(FakeMovieRepository(listOf(sintel, tears)))
            advanceUntilIdle()
            viewModel.onGenreSelected("科幻")
            viewModel.onQueryChange("zzz")
            assertTrue((viewModel.uiState.value as HomeUiState.Success).movies.isEmpty())

            viewModel.clearFilters()

            val state = viewModel.uiState.value as HomeUiState.Success
            assertEquals(listOf(sintel, tears), state.movies)
            assertEquals("", state.query)
            assertNull(state.selectedGenre)
        }

    @Test
    fun `retry keeps the selected genre`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val sintel = sampleMovie(id = "sintel", genres = listOf("动画"))
            val tears = sampleMovie(
                id = "tears",
                title = "Tears of Steel",
                genres = listOf("科幻"),
            )
            val repository = FakeMovieRepository(listOf(sintel, tears))
            val viewModel = HomeViewModel(repository)
            advanceUntilIdle()
            viewModel.onGenreSelected("科幻")
            viewModel.onQueryChange("Tears")

            repository.failure = IllegalStateException("offline")
            viewModel.loadMovies()
            advanceUntilIdle()
            assertEquals(HomeUiState.Error("offline"), viewModel.uiState.value)

            repository.failure = null
            viewModel.loadMovies()
            advanceUntilIdle()

            val state = viewModel.uiState.value as HomeUiState.Success
            assertEquals(listOf(tears), state.movies)
            assertEquals("Tears", state.query)
            assertEquals("科幻", state.selectedGenre)
        }

    @Test
    fun `retry loads the list again`() = runTest(mainDispatcherRule.dispatcher.scheduler) {
        val repository = FakeMovieRepository(failure = IllegalStateException("offline"))
        val viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        assertEquals(HomeUiState.Error("offline"), viewModel.uiState.value)

        viewModel.loadMovies()

        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()
        assertEquals(HomeUiState.Error("offline"), viewModel.uiState.value)
    }
}
