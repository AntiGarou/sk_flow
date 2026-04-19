package com.soundflow.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.soundflow.app.presentation.screens.home.HomeScreen
import com.soundflow.app.presentation.screens.library.LibraryScreen
import com.soundflow.app.presentation.screens.search.SearchScreen
import com.soundflow.app.presentation.screens.settings.SettingsScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onTrackClick: (com.soundflow.app.domain.model.Track) -> Unit
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(Screen.Home.route, Screen.Search.route, Screen.Library.route, Screen.Settings.route)) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(300)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(300)) },
            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(300)) }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onTrackClick = onTrackClick,
                    onSeeAllClick = { }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(onTrackClick = onTrackClick)
            }
            composable(Screen.Library.route) {
                LibraryScreen(
                    onPlaylistClick = { },
                    onTrackClick = onTrackClick
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
