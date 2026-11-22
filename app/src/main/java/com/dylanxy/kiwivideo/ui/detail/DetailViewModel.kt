package com.dylanxy.kiwivideo.ui.detail

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

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(val movie: Movie) : DetailUiState

    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    private val movieId: String,
    private val repository: MovieRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadMovie()
    }

    fun loadMovie() {
        _uiState.value = DetailUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                val movie = repository.getMovie(movieId)
                if (movie == null) {
                    DetailUiState.Error("影片不存在")
                } else {
                    DetailUiState.Success(movie)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                DetailUiState.Error(e.message ?: "加载失败，请重试")
            }
        }
    }

    companion object {
        fun factory(movieId: String, repository: MovieRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { DetailViewModel(movieId, repository) }
            }
    }
}
