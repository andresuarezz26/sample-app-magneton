package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.myapplication.home.HomeScreen
import com.example.myapplication.profile.ProfileScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.upload.ProcessingScreen
import com.example.myapplication.upload.UploadScreen

sealed interface Screen {
    data object Home : Screen
    data object Upload : Screen
    data class Processing(val paperId: String) : Screen
    data object Profile : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
private fun AppNavigation() {
    var currentScreen: Screen by remember { mutableStateOf(Screen.Home) }

    when (currentScreen) {
        Screen.Home -> {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { currentScreen = Screen.Upload },
                        modifier = Modifier.testTag("fab_upload")
                    ) {
                        Text("+")
                    }
                }
            ) { _ ->
                HomeScreen()
            }
        }
        Screen.Upload -> {
            UploadScreen(
                onPaperUploaded = { paperId ->
                    currentScreen = Screen.Processing(paperId)
                },
                onBack = { currentScreen = Screen.Home }
            )
        }
        is Screen.Processing -> {
            ProcessingScreen(
                paperId = (currentScreen as Screen.Processing).paperId,
                onCompleted = { currentScreen = Screen.Profile },
                onBack = { currentScreen = Screen.Home }
            )
        }
        Screen.Profile -> {
            ProfileScreen(
                onBack = { currentScreen = Screen.Home }
            )
        }
    }
}
