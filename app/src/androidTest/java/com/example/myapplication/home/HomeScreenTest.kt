package com.example.myapplication.home

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import com.example.myapplication.MainActivity
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun waitForFeedToLoad() {
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithTag("loading_indicator").fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun appLaunches_andShowsFeed() {
        waitForFeedToLoad()

        composeTestRule.onNodeWithTag("loading_indicator").assertDoesNotExist()
        composeTestRule.onNodeWithText("@astro_facts").assertExists()
        composeTestRule
            .onNodeWithText(
                "Did you know black holes emit Hawking radiation? The universe is wild! #space #physics",
                substring = true
            )
            .assertExists()
    }

    @Test
    fun tappingLike_incrementsLikeCount() {
        waitForFeedToLoad()

        composeTestRule.onNodeWithContentDescription("likes_48200").assertExists()

        composeTestRule.onNodeWithTag("like_button").performClick()

        composeTestRule.onNodeWithContentDescription("likes_48201").assertExists()
    }

    @Test
    fun pager_swipesToNextVideo() {
        waitForFeedToLoad()

        composeTestRule.onNodeWithText("@astro_facts").assertExists()

        composeTestRule.onNodeWithTag("loading_indicator").assertDoesNotExist()
        composeTestRule.onRoot().performTouchInput { swipeUp() }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("@quantum_lab").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("@quantum_lab").assertExists()
    }
}
