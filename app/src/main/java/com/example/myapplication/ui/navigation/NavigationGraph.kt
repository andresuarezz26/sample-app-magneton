package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.home.HomeScreen
import com.example.myapplication.ui.screens.LogoutScreen
import com.example.myapplication.ui.screens.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val LOGOUT = "logout"
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToLogout = {
                    navController.navigate(Routes.LOGOUT)
                }
            )
        }
        composable(Routes.LOGOUT) {
            LogoutScreen(
                onCancel = { navController.popBackStack() },
                onConfirm = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGOUT) { inclusive = true }
                    }
                }
            )
        }
    }
}
