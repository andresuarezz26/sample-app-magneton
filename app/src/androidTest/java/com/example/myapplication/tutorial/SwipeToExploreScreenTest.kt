package com.example.myapplication.tutorial

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SwipeToExploreScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun headlineAndButtons_areDisplayed() {
        composeTestRule.setContent {
            SwipeToExploreScreen()
        }

        composeTestRule.onNodeWithTag("tutorial_step2_headline").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tutorial_step2_next").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tutorial_step2_skip").assertIsDisplayed()
    }

    @Test
    fun clickingNext_invokesOnNext() {
        var nextClicked = false
        composeTestRule.setContent {
            SwipeToExploreScreen(onNext = { nextClicked = true })
        }

        composeTestRule.onNodeWithTag("tutorial_step2_next").performClick()

        assertTrue(nextClicked)
    }

    @Test
    fun clickingSkip_invokesOnSkip() {
        var skipClicked = false
        composeTestRule.setContent {
            SwipeToExploreScreen(onSkip = { skipClicked = true })
        }

        composeTestRule.onNodeWithTag("tutorial_step2_skip").performClick()

        assertTrue(skipClicked)
    }
}
