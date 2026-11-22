package com.dylanxy.kiwivideo.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dylanxy.kiwivideo.data.model.Movie
import com.dylanxy.kiwivideo.ui.components.ErrorState
import com.dylanxy.kiwivideo.ui.components.LoadingState
import com.dylanxy.kiwivideo.ui.components.MoviePoster

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMovieClick: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val success = uiState as? HomeUiState.Success

    Scaffold(
        topBar = {
            HomeTopBar(
                success = success,
                onQueryChange = viewModel::onQueryChange,
                onGenreSelected = viewModel::onGenreSelected,
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            HomeUiState.Loading -> LoadingState(Modifier.padding(innerPadding))

            is HomeUiState.Error -> ErrorState(
                message = state.message,
                onRetry = viewModel::loadMovies,
                modifier = Modifier.padding(innerPadding),
            )

            is HomeUiState.Success -> if (
                state.movies.isEmpty() &&
                (state.query.isNotBlank() || state.selectedGenre != null)
            ) {
                EmptySearchState(
                    onClearFilters = viewModel::clearFilters,
                    modifier = Modifier.padding(innerPadding),
                )
            } else {
                MovieGrid(
                    movies = state.movies,
                    onMovieClick = onMovieClick,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    success: HomeUiState.Success?,
    onQueryChange: (String) -> Unit,
    onGenreSelected: (String?) -> Unit,
) {
    Column {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Kiwi Video",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "开源电影 · MVP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
        )
        if (success != null) {
            OutlinedTextField(
                value = success.query,
                onValueChange = onQueryChange,
                label = { Text("搜索影片") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            GenreFilterRow(
                genres = success.genres,
                selectedGenre = success.selectedGenre,
                onGenreSelected = onGenreSelected,
            )
        }
    }
}

@Composable
private fun GenreFilterRow(
    genres: List<String>,
    selectedGenre: String?,
    onGenreSelected: (String?) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GenreChip(
            label = "全部",
            selected = selectedGenre == null,
            onClick = { onGenreSelected(null) },
        )
        genres.forEach { genre ->
            GenreChip(
                label = genre,
                selected = selectedGenre == genre,
                onClick = { onGenreSelected(genre) },
            )
        }
    }
}

@Composable
private fun GenreChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
    )
}

@Composable
private fun EmptySearchState(
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "没有匹配的影片",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onClearFilters) {
            Text("清除筛选")
        }
    }
}

@Composable
private fun MovieGrid(
    movies: List<Movie>,
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieCard(
                movie = movie,
                onClick = { onMovieClick(movie.id) },
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "演示片源：Blender 基金会开源电影（CC BY）",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        MoviePoster(
            movie = movie,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "${movie.year} · ${movie.durationMinutes} 分钟",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
