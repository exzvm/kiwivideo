package com.dylanxy.kiwivideo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dylanxy.kiwivideo.data.MovieRepository
import com.dylanxy.kiwivideo.data.model.Movie
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val movies: List<Movie>,
        val query: String = "",
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var catalog: List<Movie> = emptyList()
    private var query: String = ""

    init {
        loadMovies()
    }

    fun loadMovies() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                catalog = repository.getMovies()
                successState()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                HomeUiState.Error(e.message ?: "加载失败，请重试")
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        query = newQuery
        if (_uiState.value is HomeUiState.Success) {
            _uiState.value = successState()
        }
    }

    private fun successState(): HomeUiState.Success =
        HomeUiState.Success(applyQuery(catalog), query)

    private fun applyQuery(movies: List<Movie>): List<Movie> {
        val trimmed = query.trim()
        return if (trimmed.isEmpty()) {
            movies
        } else {
            movies.filter { it.title.contains(trimmed, ignoreCase = true) }
        }
    }

    companion object {
        fun factory(repository: MovieRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(repository) }
        }
    }
}
