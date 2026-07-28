package com.example.myapplication.home

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenEndToEndTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun endToEnd_feedLoadsAndLikeIncrementsCount() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag("video_author").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("video_author")
            .assertIsDisplayed()
            .assertTextEquals("@astro_facts")

        composeRule.onNodeWithTag("video_description")
            .assertIsDisplayed()

        val likeButton = composeRule.onNodeWithTag("like_button", useUnmergedTree = true)
        val initialDescription = likeButton
            .fetchSemanticsNode()
            .config[SemanticsProperties.ContentDescription]
            .first()

        assert(initialDescription == "48200 likes") {
            "Expected initial like content description to be '48200 likes' but was '$initialDescription'"
        }

        composeRule.onNodeWithTag("like_button").performClick()

        val updatedDescription = composeRule
            .onNodeWithTag("like_button", useUnmergedTree = true)
            .fetchSemanticsNode()
            .config[SemanticsProperties.ContentDescription]
            .first()

        assert(updatedDescription == "48201 likes") {
            "Expected updated like content description to be '48201 likes' but was '$updatedDescription'"
        }
    }
}
