package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.myapplication.comment.CommentScreen
import com.example.myapplication.home.HomeScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.upload.UploadScreen

private enum class Screen { Home, Upload, Comments }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf(Screen.Home) }
                when (currentScreen) {
                    Screen.Home -> HomeScreen(
                        onUploadClick = { currentScreen = Screen.Upload },
                        onCommentsClick = { currentScreen = Screen.Comments }
                    )
                    Screen.Upload -> UploadScreen(onBack = { currentScreen = Screen.Home })
                    Screen.Comments -> CommentScreen(onBack = { currentScreen = Screen.Home })
                }
            }
        }
    }
}
