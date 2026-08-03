package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.myapplication.home.HomeScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.upload.UploadScreen

private enum class Screen { Home, Upload }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf(Screen.Home) }
                Box {
                    when (currentScreen) {
                        Screen.Home -> HomeScreen(onUploadClick = { currentScreen = Screen.Upload })
                        Screen.Upload -> UploadScreen(onBack = { currentScreen = Screen.Home })
                    }
                    Text(
                        text = stringResource(R.string.hello_world),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
