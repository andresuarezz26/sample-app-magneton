package com.example.myapplication.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation test that launches [HomeScreen] with the real [HomeViewModel]
 * (backed by the default feed use case) and verifies that swiping the vertical
 * pager changes the displayed video from the first feed item to the second.
 */
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun swipingPager_changesVideoFromFirstToSecond() {
        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(viewModel = HomeViewModel())
            }
        }

        // The ViewModel first shows a loading spinner, then loads the feed
        // asynchronously. Wait until the first video's author is displayed.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(FIRST_AUTHOR).fetchSemanticsNodes().isNotEmpty()
        }

        // The first video (id "1") is shown.
        composeTestRule.onNodeWithText(FIRST_AUTHOR).assertIsDisplayed()

        // Swipe up on the vertical pager to advance to the next video.
        composeTestRule.onRoot().performTouchInput { swipeUp() }

        // The second video (id "2") is now shown and the first is gone.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(SECOND_AUTHOR).fetchSemanticsNodes().isNotEmpty() &&
                composeTestRule.onAllNodesWithText(FIRST_AUTHOR).fetchSemanticsNodes().isEmpty()
        }

        composeTestRule.onNodeWithText(SECOND_AUTHOR).assertIsDisplayed()
    }

    private companion object {
        const val FIRST_AUTHOR = "@astro_facts"
        const val SECOND_AUTHOR = "@quantum_lab"
    }
}
