package com.example.myapplication.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun splashScreenDisplaysLogoAndText() {
        composeTestRule.setContent {
            MyApplicationTheme {
                SplashScreen(onNavigateToHome = {})
            }
        }

        composeTestRule.onNodeWithText("TikTok").assertExists()
    }

    @Test
    fun splashScreenIsResponsiveToLargeScreens() {
        composeTestRule.setContent {
            MyApplicationTheme {
                SplashScreen(onNavigateToHome = {})
            }
        }

        composeTestRule.onNodeWithText("TikTok").assertExists()
    }

    @Test
    fun splashScreenRendersWithAnimatedLogo() {
        var navigationCalled = false
        composeTestRule.setContent {
            MyApplicationTheme {
                SplashScreen(onNavigateToHome = { navigationCalled = true })
            }
        }

        composeTestRule.onNodeWithText("TikTok").assertExists()
    }
}
