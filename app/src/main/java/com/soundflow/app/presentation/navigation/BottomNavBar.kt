package com.soundflow.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.soundflow.app.presentation.theme.Primary
import com.soundflow.app.presentation.theme.Surface
import com.soundflow.app.presentation.theme.OnSurfaceVariant

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(Screen.Home, "Home") {
            Icon(Icons.Filled.Home, contentDescription = "Home")
        },
        BottomNavItem(Screen.Search, "Search") {
            Icon(Icons.Filled.Search, contentDescription = "Search")
        },
        BottomNavItem(Screen.Library, "Library") {
            Icon(Icons.Filled.LibraryMusic, contentDescription = "Library")
        },
        BottomNavItem(Screen.Settings, "Settings") {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
            NavigationBarItem(
                icon = item.icon,
                label = { Text(item.label) },
                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = OnSurfaceVariant,
                    unselectedTextColor = OnSurfaceVariant,
                    indicatorColor = Primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
