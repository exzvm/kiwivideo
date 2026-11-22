package com.dylanxy.kiwivideo.ui.home

import com.dylanxy.kiwivideo.FakeMovieRepository
import com.dylanxy.kiwivideo.MainDispatcherRule
import com.dylanxy.kiwivideo.sampleMovie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
        assertEquals(HomeUiState.Success(movies), viewModel.uiState.value)
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
                HomeUiState.Success(listOf(bunny), query = "BUNNY"),
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

        assertEquals(HomeUiState.Success(movies, query = "   "), viewModel.uiState.value)
    }

    @Test
    fun `no matches yields an empty list`() = runTest(mainDispatcherRule.dispatcher.scheduler) {
        val viewModel = HomeViewModel(
            FakeMovieRepository(listOf(sampleMovie(title = "Sintel"))),
        )
        advanceUntilIdle()

        viewModel.onQueryChange("zzz")

        assertEquals(
            HomeUiState.Success(emptyList(), query = "zzz"),
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
                HomeUiState.Success(listOf(bunny), query = "bunny"),
                viewModel.uiState.value,
            )
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
