package com.example.myapplication.discover

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiscoverHappyPathTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun discoverFeed_loadsAndDisplaysContent() {
        composeTestRule.onNodeWithTag("navDiscover").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("discoverFeed").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("discoverFeed").assertExists()
        composeTestRule.onAllNodesWithTag("paperCard").onFirst().assertExists()
        composeTestRule.onAllNodesWithTag("paperTitle").onFirst().assertExists()
        composeTestRule.onAllNodesWithTag("fieldTag").onFirst().assertExists()
    }

    @Test
    fun discoverFeed_filterByCategory_showsFilteredResults() {
        composeTestRule.onNodeWithTag("navDiscover").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("categoryChip_Physics").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("categoryChip_Physics").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("discoverFeed").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("discoverFeed").assertExists()
        composeTestRule.onAllNodesWithTag("paperCard").onFirst().assertExists()
        composeTestRule.onAllNodesWithTag("errorView").assertCountEquals(0)
    }

    @Test
    fun discoverFeed_scroll_loadNextPage() {
        composeTestRule.onNodeWithTag("navDiscover").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("discoverFeed").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("discoverFeed").performScrollToIndex(9)

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("paperCard").fetchSemanticsNodes().size > 10
        }

        composeTestRule.onNodeWithTag("discoverFeed").assertExists()
        composeTestRule.onAllNodesWithTag("errorView").assertCountEquals(0)
    }
}
