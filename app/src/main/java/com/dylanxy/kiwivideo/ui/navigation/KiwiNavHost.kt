package com.dylanxy.kiwivideo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dylanxy.kiwivideo.data.MovieRepository
import com.dylanxy.kiwivideo.ui.detail.DetailScreen
import com.dylanxy.kiwivideo.ui.detail.DetailViewModel
import com.dylanxy.kiwivideo.ui.home.HomeScreen
import com.dylanxy.kiwivideo.ui.home.HomeViewModel
import com.dylanxy.kiwivideo.ui.player.PlayerScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class DetailRoute(val movieId: String)

@Serializable
data class PlayerRoute(val movieId: String)

@Composable
fun KiwiNavHost(repository: MovieRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
    ) {
        composable<HomeRoute> {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(repository),
            )
            HomeScreen(
                viewModel = viewModel,
                onMovieClick = { movieId ->
                    navController.navigate(DetailRoute(movieId))
                },
            )
        }

        composable<DetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DetailRoute>()
            val viewModel: DetailViewModel = viewModel(
                key = "detail:${route.movieId}",
                factory = DetailViewModel.factory(route.movieId, repository),
            )
            DetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onPlay = { movie ->
                    navController.navigate(PlayerRoute(movie.id))
                },
            )
        }

        composable<PlayerRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<PlayerRoute>()
            val viewModel: DetailViewModel = viewModel(
                key = "player:${route.movieId}",
                factory = DetailViewModel.factory(route.movieId, repository),
            )
            PlayerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
