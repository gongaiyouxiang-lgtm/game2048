package com.codebythura.fruit2048.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codebythura.fruit2048.data.GRID_SIZE
import com.codebythura.fruit2048.ui.home.GRID_SIZE_ARG
import com.codebythura.fruit2048.ui.home.GameScreen
import com.codebythura.fruit2048.ui.home.RESUME_ARG
import com.codebythura.fruit2048.ui.landing.LandingScreen
import com.codebythura.fruit2048.ui.settings.SettingsScreen

private const val ROUTE_LANDING = "landing"
private const val ROUTE_GAME = "game"
private const val ROUTE_SETTINGS = "settings"

@Composable
fun Fruit2048(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ROUTE_LANDING,
        modifier = modifier,
    ) {
        composable(ROUTE_LANDING) {
            LandingScreen(
                onStartGame = { gridSize ->
                    navController.navigate("$ROUTE_GAME/$gridSize?$RESUME_ARG=false")
                },
                onContinueGame = { gridSize ->
                    navController.navigate("$ROUTE_GAME/$gridSize?$RESUME_ARG=true")
                },
                onOpenSettings = { navController.navigate(ROUTE_SETTINGS) },
            )
        }
        composable(ROUTE_SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = "$ROUTE_GAME/{$GRID_SIZE_ARG}?$RESUME_ARG={$RESUME_ARG}",
            arguments = listOf(
                navArgument(GRID_SIZE_ARG) {
                    type = NavType.IntType
                    defaultValue = GRID_SIZE
                },
                navArgument(RESUME_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                },
            ),
        ) {
            GameScreen(onBack = { navController.popBackStack() })
        }
    }
}
