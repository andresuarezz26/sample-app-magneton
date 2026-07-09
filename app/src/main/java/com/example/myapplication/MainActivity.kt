package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.myapplication.home.HomeScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.upload.UploadScreen
import com.example.myapplication.upload.UploadTestTags

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppRoot()
            }
        }
    }
}

@Composable
private fun AppRoot() {
    var showUpload by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreen()

        if (showUpload) {
            Surface(modifier = Modifier.fillMaxSize()) {
                // A fresh UploadScreen (with its own ViewModel) each time the flow opens.
                UploadScreen(onClose = { showUpload = false })
            }
        } else {
            ExtendedFloatingActionButton(
                onClick = { showUpload = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(24.dp)
                    .testTag(UploadTestTags.FAB_UPLOAD)
            ) {
                Text("+ Upload")
            }
        }
    }
}
