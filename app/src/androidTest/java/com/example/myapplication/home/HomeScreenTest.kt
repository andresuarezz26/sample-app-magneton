package com.example.myapplication.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleVideo = VideoItem(
        id = "1",
        author = "@astro_facts",
        description = "desc",
        likes = 1,
        comments = 1,
        shares = 1,
        music = "music",
        backgroundColorHex = 0xFF1A1A2E
    )

    @Test
    fun logoutButton_isDisplayed_andInvokesCallback() {
        var logoutClicked = false
        composeTestRule.setContent {
            MyApplicationTheme {
                HomeContent(
                    state = HomeUiState(videos = listOf(sampleVideo)),
                    onIntent = {},
                    onLogoutClick = { logoutClicked = true }
                )
            }
        }
        composeTestRule.onNodeWithText("Logout").assertIsDisplayed().performClick()
        assert(logoutClicked)
    }
}
