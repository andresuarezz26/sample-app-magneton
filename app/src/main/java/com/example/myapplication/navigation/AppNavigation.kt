package com.example.myapplication.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.onboarding.OnboardingPreferences
import com.example.myapplication.home.HomeScreen
import com.example.myapplication.onboarding.InstitutionalEmailScreen
import com.example.myapplication.onboarding.InterestSelectionScreen
import com.example.myapplication.onboarding.OnboardingIntent
import com.example.myapplication.onboarding.OnboardingViewModel
import com.example.myapplication.onboarding.WelcomeScreen

private const val ONBOARDING_GRAPH_ROUTE = "onboarding"
private const val WELCOME_ROUTE = "onboarding/welcome"
private const val INTERESTS_ROUTE = "onboarding/interests"
private const val EMAIL_ROUTE = "onboarding/email"
private const val HOME_ROUTE = "home"

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val onboardingPreferences = remember { OnboardingPreferences(context.applicationContext) }
    val isOnboardingComplete by onboardingPreferences.isOnboardingComplete.collectAsStateWithLifecycle(initialValue = null)
    val startDestinationComplete = isOnboardingComplete

    if (startDestinationComplete == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (startDestinationComplete) HOME_ROUTE else ONBOARDING_GRAPH_ROUTE
    ) {
        navigation(startDestination = WELCOME_ROUTE, route = ONBOARDING_GRAPH_ROUTE) {
            composable(WELCOME_ROUTE) {
                WelcomeScreen(onGetStarted = { navController.navigate(INTERESTS_ROUTE) })
            }
            composable(INTERESTS_ROUTE) { entry ->
                val viewModel = rememberOnboardingViewModel(entry, navController, onboardingPreferences)
                val state by viewModel.state.collectAsStateWithLifecycle()
                InterestSelectionScreen(
                    state = state,
                    onToggleField = { viewModel.onIntent(OnboardingIntent.ToggleField(it)) },
                    onContinue = { navController.navigate(EMAIL_ROUTE) }
                )
            }
            composable(EMAIL_ROUTE) { entry ->
                val viewModel = rememberOnboardingViewModel(entry, navController, onboardingPreferences)
                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(state.isComplete) {
                    if (state.isComplete) {
                        navController.navigate(HOME_ROUTE) {
                            popUpTo(ONBOARDING_GRAPH_ROUTE) { inclusive = true }
                        }
                    }
                }

                InstitutionalEmailScreen(
                    state = state,
                    onEmailChanged = { viewModel.onIntent(OnboardingIntent.EmailChanged(it)) },
                    onSubmit = { viewModel.onIntent(OnboardingIntent.SubmitEmail) },
                    onSkip = { viewModel.onIntent(OnboardingIntent.SkipEmail) }
                )
            }
        }
        composable(HOME_ROUTE) {
            HomeScreen()
        }
    }
}

@Composable
private fun rememberOnboardingViewModel(
    entry: NavBackStackEntry,
    navController: NavHostController,
    onboardingPreferences: OnboardingPreferences
): OnboardingViewModel {
    val parentEntry = remember(entry) {
        navController.getBackStackEntry(ONBOARDING_GRAPH_ROUTE)
    }
    return viewModel(parentEntry, factory = OnboardingViewModel.factory(onboardingPreferences))
}
