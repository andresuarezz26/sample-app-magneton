package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.myapplication.discover.DiscoverScreen
import com.example.myapplication.home.HomeScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

private enum class MainTab { HOME, DISCOVER }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScaffold()
            }
        }
    }
}

@Composable
private fun MainScaffold() {
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == MainTab.HOME,
                    onClick = { selectedTab = MainTab.HOME },
                    icon = { Text("🏠") },
                    label = { Text("Home") },
                    modifier = Modifier.testTag("navHome")
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.DISCOVER,
                    onClick = { selectedTab = MainTab.DISCOVER },
                    icon = { Text("🔭") },
                    label = { Text("Discover") },
                    modifier = Modifier.testTag("navDiscover")
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            // HomeScreen renders its own immersive, edge-to-edge video background and
            // applies statusBarsPadding()/navigationBarsPadding() only to specific overlay
            // elements, so it must ignore innerPadding to avoid double-applying system insets.
            MainTab.HOME -> HomeScreen()
            MainTab.DISCOVER -> Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                DiscoverScreen()
            }
        }
    }
}
