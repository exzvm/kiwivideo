package com.dylanxy.kiwivideo.ui.detail

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
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `emits the requested movie`() = runTest(mainDispatcherRule.dispatcher.scheduler) {
        val movie = sampleMovie(id = "sintel")
        val viewModel = DetailViewModel(
            movieId = "sintel",
            repository = FakeMovieRepository(listOf(movie)),
        )

        assertEquals(DetailUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()
        assertEquals(DetailUiState.Success(movie), viewModel.uiState.value)
    }

    @Test
    fun `emits error when the movie is missing`() = runTest(mainDispatcherRule.dispatcher.scheduler) {
        val viewModel = DetailViewModel(
            movieId = "missing",
            repository = FakeMovieRepository(listOf(sampleMovie())),
        )

        advanceUntilIdle()

        assertEquals(DetailUiState.Error("影片不存在"), viewModel.uiState.value)
    }

    @Test
    fun `emits error when the repository fails`() = runTest(mainDispatcherRule.dispatcher.scheduler) {
        val viewModel = DetailViewModel(
            movieId = "sintel",
            repository = FakeMovieRepository(failure = IllegalStateException("offline")),
        )

        advanceUntilIdle()

        assertEquals(DetailUiState.Error("offline"), viewModel.uiState.value)
    }
}
