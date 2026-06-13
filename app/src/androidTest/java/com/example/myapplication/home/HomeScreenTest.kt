package com.example.myapplication.home

import androidx.compose.ui.test.ComposeTestRule
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule: ComposeTestRule = createComposeRule()

    @Test
    fun testFeedLoadsAndDisplaysFirstVideo() {
        composeTestRule.setContent {
            HomeScreen()
        }

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.onNodeWithText("@astro_facts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Did you know black holes emit Hawking radiation?")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("48.2K").assertIsDisplayed()
    }

    @Test
    fun testUIElementsAreDisplayed() {
        composeTestRule.setContent {
            HomeScreen()
        }

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.onNodeWithText("Following").assertIsDisplayed()
        composeTestRule.onNodeWithText("For You").assertIsDisplayed()

        composeTestRule.onNodeWithText("❤️").assertIsDisplayed()
        composeTestRule.onNodeWithText("💬").assertIsDisplayed()
        composeTestRule.onNodeWithText("➡️").assertIsDisplayed()

        composeTestRule.onNodeWithText("♪").assertIsDisplayed()
    }

    @Test
    fun testLikeButtonIncrementsCount() {
        composeTestRule.setContent {
            HomeScreen()
        }

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.onNodeWithText("48.2K").assertIsDisplayed()

        composeTestRule.onNodeWithTag("action_item_❤️").performClick()

        composeTestRule.mainClock.advanceTimeByFrame()

        composeTestRule.onNodeWithText("48.3K").assertIsDisplayed()
    }

    @Test
    fun testVerticalPagingNavigatesToNextVideo() {
        composeTestRule.setContent {
            HomeScreen()
        }

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.onNodeWithText("@astro_facts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cosmic Vibes — Science Beats").assertIsDisplayed()

        composeTestRule.onRoot().performTouchInput { swipeUp() }

        composeTestRule.mainClock.advanceTimeByFrame()

        composeTestRule.onNodeWithText("@quantum_lab").assertIsDisplayed()
        composeTestRule.onNodeWithText("Wave Function — Quantum Sounds").assertIsDisplayed()
    }

    @Test
    fun testInitialStateShowsCorrectCounts() {
        composeTestRule.setContent {
            HomeScreen()
        }

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.onNodeWithText("48.2K").assertIsDisplayed()
        composeTestRule.onNodeWithText("1.2K").assertIsDisplayed()
        composeTestRule.onNodeWithText("892").assertIsDisplayed()
    }

    @Test
    fun testMultipleLikeClicksIncrementCountCorrectly() {
        composeTestRule.setContent {
            HomeScreen()
        }

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.onNodeWithText("48.2K").assertIsDisplayed()

        composeTestRule.onNodeWithTag("action_item_❤️").performClick()
        composeTestRule.mainClock.advanceTimeByFrame()

        composeTestRule.onNodeWithText("48.3K").assertIsDisplayed()

        composeTestRule.onNodeWithTag("action_item_❤️").performClick()
        composeTestRule.mainClock.advanceTimeByFrame()

        composeTestRule.onNodeWithText("48.4K").assertIsDisplayed()
    }

    @Test
    fun testAuthorAvatarDisplays() {
        composeTestRule.setContent {
            HomeScreen()
        }

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.onNodeWithText("AS").assertIsDisplayed()
    }

    @Test
    fun testMusicTitleDisplaysCorrectly() {
        composeTestRule.setContent {
            HomeScreen()
        }

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.onNodeWithText("♫  Cosmic Vibes — Science Beats").assertIsDisplayed()
    }

    @Test
    fun testLoadingStateTransitionsToContent() {
        composeTestRule.setContent {
            HomeScreen()
        }

        composeTestRule.onNodeWithText("@astro_facts").assertDoesNotExist()

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.onNodeWithText("48.2K").assertIsDisplayed()
        composeTestRule.onNodeWithText("@astro_facts").assertIsDisplayed()
    }

    @Test
    fun testDescriptionTextIsDisplayed() {
        composeTestRule.setContent {
            HomeScreen()
        }

        composeTestRule.mainClock.advanceTimeBy(500)

        composeTestRule.onNodeWithText("Did you know black holes emit Hawking radiation?")
            .assertIsDisplayed()
    }
}
