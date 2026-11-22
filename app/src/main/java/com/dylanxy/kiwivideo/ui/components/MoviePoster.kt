package com.dylanxy.kiwivideo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dylanxy.kiwivideo.data.model.Movie

private val PosterPalettes = listOf(
    listOf(Color(0xFF2C2233), Color(0xFF120D17)),
    listOf(Color(0xFF1B3040), Color(0xFF08141C)),
    listOf(Color(0xFF3A2A1A), Color(0xFF160F08)),
    listOf(Color(0xFF1F3527), Color(0xFF0A140E)),
    listOf(Color(0xFF35202B), Color(0xFF140A0F)),
)

/**
 * 海报图。远程图片加载中或失败时，显示由影片 id 决定的渐变占位图。
 */
@Composable
fun MoviePoster(
    movie: Movie,
    modifier: Modifier = Modifier,
) {
    val palette = remember(movie.id) {
        PosterPalettes[Math.floorMod(movie.id.hashCode(), PosterPalettes.size)]
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(palette)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = movie.title,
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp),
        )
        if (movie.posterUrl.isNotBlank()) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}
