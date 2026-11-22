package com.dylanxy.kiwivideo.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dylanxy.kiwivideo.data.model.Movie
import com.dylanxy.kiwivideo.ui.components.ErrorState
import com.dylanxy.kiwivideo.ui.components.LoadingState
import com.dylanxy.kiwivideo.ui.components.MoviePoster

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onPlay: (Movie) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("影片详情") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("‹ 返回")
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            DetailUiState.Loading -> LoadingState(Modifier.padding(innerPadding))

            is DetailUiState.Error -> ErrorState(
                message = state.message,
                onRetry = viewModel::loadMovie,
                modifier = Modifier.padding(innerPadding),
            )

            is DetailUiState.Success -> DetailContent(
                movie = state.movie,
                onPlay = onPlay,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun DetailContent(
    movie: Movie,
    onPlay: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        MoviePoster(
            movie = movie,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = buildList {
                add(movie.year.toString())
                add("${movie.durationMinutes} 分钟")
                addAll(movie.genres)
            }.joinToString(" · "),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = movie.synopsis,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 24.sp,
        )
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = { onPlay(movie) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Text("▶  播放", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "片源：Blender 基金会开源电影（CC BY）",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
    }
}
