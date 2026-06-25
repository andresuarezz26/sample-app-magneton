package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.myapplication.data.local.OnboardingRepository
import com.example.myapplication.data.local.dataStore
import com.example.myapplication.navigation.AppNavHost
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = OnboardingRepository(dataStore)

        setContent {
            MyApplicationTheme {
                // Resolve the persisted onboarding flag off the main thread: produceState
                // launches a coroutine and collects the DataStore flow, keeping onCreate
                // non-blocking. Until the first value arrives we show a brief loading state.
                val onboardingComplete by produceState<Boolean?>(initialValue = null, repository) {
                    value = repository.isOnboardingComplete.first()
                }

                when (val complete = onboardingComplete) {
                    null -> Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    else -> AppNavHost(
                        onboardingComplete = complete,
                        onboardingRepository = repository,
                        onExitApp = { finish() }
                    )
                }
            }
        }
    }
}
