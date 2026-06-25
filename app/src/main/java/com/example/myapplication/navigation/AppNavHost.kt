package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.myapplication.data.local.OnboardingRepository
import com.example.myapplication.home.HomeScreen
import com.example.myapplication.onboarding.InstitutionalEmailScreen
import com.example.myapplication.onboarding.InterestsScreen
import com.example.myapplication.onboarding.OnboardingIntent
import com.example.myapplication.onboarding.OnboardingViewModel
import com.example.myapplication.onboarding.WelcomeScreen
import kotlinx.coroutines.launch

object Routes {
    const val ONBOARDING_GRAPH = "onboarding"
    const val WELCOME = "welcome"
    const val INTERESTS = "interests"
    const val EMAIL = "email"
    const val DISCOVER = "discover"
}

@Composable
fun AppNavHost(
    onboardingComplete: Boolean,
    onboardingRepository: OnboardingRepository,
    onExitApp: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()

    fun completeAndGoToDiscover(viewModel: OnboardingViewModel) {
        // Persist interests AND the onboarding_complete flag, awaiting both writes
        // before navigating, so a subsequent cold start reliably skips onboarding.
        scope.launch {
            viewModel.saveInterests()
            onboardingRepository.setOnboardingComplete(true)
            navController.navigate(Routes.DISCOVER) {
                popUpTo(Routes.ONBOARDING_GRAPH) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (onboardingComplete) Routes.DISCOVER else Routes.ONBOARDING_GRAPH
    ) {
        navigation(startDestination = Routes.WELCOME, route = Routes.ONBOARDING_GRAPH) {
            composable(Routes.WELCOME) { backStackEntry ->
                // Tapping Back on the welcome screen exits gracefully instead of looping.
                androidx.activity.compose.BackHandler { onExitApp() }
                WelcomeScreen(
                    onGetStarted = { navController.navigate(Routes.INTERESTS) }
                )
            }
            composable(Routes.INTERESTS) { backStackEntry ->
                val viewModel = backStackEntry.onboardingViewModel(navController)
                val state by viewModel.state.collectAsStateWithLifecycle()
                InterestsScreen(
                    state = state,
                    onToggleField = { viewModel.onIntent(OnboardingIntent.ToggleField(it)) },
                    onContinue = { navController.navigate(Routes.EMAIL) }
                )
            }
            composable(Routes.EMAIL) { backStackEntry ->
                val viewModel = backStackEntry.onboardingViewModel(navController)
                val state by viewModel.state.collectAsStateWithLifecycle()
                InstitutionalEmailScreen(
                    email = state.institutionalEmail,
                    onEmailChange = { viewModel.onIntent(OnboardingIntent.UpdateEmail(it)) },
                    // Connect keeps the entered email; Skip discards it so the optional
                    // step's two actions persist genuinely different state.
                    onConnect = { completeAndGoToDiscover(viewModel) },
                    onSkip = {
                        viewModel.onIntent(OnboardingIntent.UpdateEmail(""))
                        completeAndGoToDiscover(viewModel)
                    }
                )
            }
        }
        composable(Routes.DISCOVER) {
            HomeScreen()
        }
    }
}

/**
 * Returns an [OnboardingViewModel] scoped to the onboarding nav graph so the same
 * instance (and its selected interests) is shared across the onboarding screens.
 */
@Composable
private fun androidx.navigation.NavBackStackEntry.onboardingViewModel(
    navController: NavHostController
): OnboardingViewModel {
    val parentEntry = navController.getBackStackEntry(Routes.ONBOARDING_GRAPH)
    return viewModel(viewModelStoreOwner = parentEntry)
}
