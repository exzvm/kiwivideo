package com.dylanxy.kiwivideo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.dylanxy.kiwivideo.data.MovieCatalogJson
import com.dylanxy.kiwivideo.data.local.LocalMovieRepository
import com.dylanxy.kiwivideo.ui.navigation.KiwiNavHost
import com.dylanxy.kiwivideo.ui.theme.KiwivideoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setSingletonImageLoaderFactory { context ->
                ImageLoader.Builder(context)
                    .components { add(OkHttpNetworkFetcherFactory()) }
                    .build()
            }

            KiwivideoTheme {
                val context = LocalContext.current
                val repositoryResult = remember {
                    runCatching {
                        val raw = context.assets
                            .open("movies.json")
                            .bufferedReader()
                            .use { it.readText() }
                        LocalMovieRepository(MovieCatalogJson.decode(raw))
                    }
                }

                repositoryResult.fold(
                    onSuccess = { repository -> KiwiNavHost(repository) },
                    onFailure = { error -> StartupError(error) },
                )
            }
        }
    }
}

@Composable
private fun StartupError(error: Throwable) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "片单加载失败",
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = error.message ?: "未知错误",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
