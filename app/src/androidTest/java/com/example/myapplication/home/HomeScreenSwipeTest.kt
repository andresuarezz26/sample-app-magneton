package com.example.myapplication.home

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenSwipeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun swipingUp6TimesOnHomeScreenPagesThroughAllVideosAndClampsOnTheLastOne() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("@astro_facts").fetchSemanticsNodes().isNotEmpty()
        }

        val expectedAuthorsAfterSwipe = listOf(
            "@quantum_lab",
            "@bio_wonders",
            "@climate_science",
            "@neuro_brain",
            "@chem_lab"
        )

        expectedAuthorsAfterSwipe.forEach { expectedAuthor ->
            composeTestRule.onRoot().performTouchInput { swipeUp() }
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(expectedAuthor).assertExists()
        }

        // 6th swipe up: already on the last page, pager should clamp and stay put, no crash.
        composeTestRule.onRoot().performTouchInput { swipeUp() }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("@chem_lab").assertExists()
    }
}
