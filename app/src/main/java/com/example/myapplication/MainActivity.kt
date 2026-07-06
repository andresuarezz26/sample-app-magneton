package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.home.HomeScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.videoplayer.VideoPlayerScreen

sealed class AppState {
    data object Home : AppState()
    data class VideoPlayer(val videos: List<VideoItem>, val initialIndex: Int) : AppState()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppContent()
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun AppContent() {
    val appState = remember { mutableStateOf<AppState>(AppState.Home) }

    when (val state = appState.value) {
        AppState.Home -> {
            HomeScreen(
                onVideoCardClick = { videos, index ->
                    appState.value = AppState.VideoPlayer(videos, index)
                }
            )
        }
        is AppState.VideoPlayer -> {
            VideoPlayerScreen(
                videos = state.videos,
                initialIndex = state.initialIndex,
                onBack = {
                    appState.value = AppState.Home
                }
            )
        }
    }
}
