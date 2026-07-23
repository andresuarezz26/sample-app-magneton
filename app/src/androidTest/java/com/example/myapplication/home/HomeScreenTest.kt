package com.example.myapplication.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.myapplication.data.model.VideoItem
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val state = HomeUiState(
        videos = listOf(
            VideoItem(
                id = "1",
                author = "@astro_facts",
                description = "Did you know black holes emit Hawking radiation?",
                likes = 100,
                comments = 10,
                shares = 5,
                music = "Cosmic Vibes",
                backgroundColorHex = 0xFF1A1A2E
            )
        )
    )

    @Test
    fun logoutButton_isDisplayed() {
        composeTestRule.setContent {
            HomeContent(state = state, onIntent = {})
        }

        composeTestRule.onNodeWithText("Logout").assertIsDisplayed()
    }

    @Test
    fun logoutButton_click_dispatchesLogoutIntent() {
        var receivedIntent: HomeIntent? = null

        composeTestRule.setContent {
            HomeContent(state = state, onIntent = { receivedIntent = it })
        }

        composeTestRule.onNodeWithText("Logout").performClick()

        assertEquals(HomeIntent.Logout, receivedIntent)
    }
}
