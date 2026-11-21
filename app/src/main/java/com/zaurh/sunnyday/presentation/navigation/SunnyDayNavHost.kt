package com.zaurh.sunnyday.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zaurh.sunnyday.presentation.search.SearchRoute
import com.zaurh.sunnyday.presentation.weather.WeatherRoute
import kotlinx.serialization.Serializable

@Serializable
data object WeatherDestination

@Serializable
data object SearchDestination

@Composable
fun SunnyDayNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = WeatherDestination) {
        composable<WeatherDestination>(
            exitTransition = { fadeOut(tween(250)) },
            popEnterTransition = { fadeIn(tween(250)) },
        ) {
            WeatherRoute(
                onNavigateToSearch = {
                    navController.navigate(SearchDestination) { launchSingleTop = true }
                },
            )
        }
        composable<SearchDestination>(
            enterTransition = { slideInVertically(tween(300)) { it / 6 } + fadeIn(tween(300)) },
            popExitTransition = { slideOutVertically(tween(250)) { it / 6 } + fadeOut(tween(250)) },
        ) {
            SearchRoute(onClose = { navController.popBackStack() })
        }
    }
}
