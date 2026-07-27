package com.example.myapplication.tutorial

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TutorialScreen3Test {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun headlineAndBodyTextAreDisplayed() {
        composeTestRule.setContent {
            MyApplicationTheme {
                TutorialScreen3(onNext = {}, onSkip = {})
            }
        }

        composeTestRule.onNodeWithTag("tutorialHeadline").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tutorialBody").assertIsDisplayed()
    }

    @Test
    fun tappingNextButtonTriggersOnNextCallback() {
        var nextCount = 0
        composeTestRule.setContent {
            MyApplicationTheme {
                TutorialScreen3(onNext = { nextCount++ }, onSkip = {})
            }
        }

        composeTestRule.onNodeWithTag("btnNextTutorial").performClick()

        assertEquals(1, nextCount)
    }

    @Test
    fun tappingSkipButtonTriggersOnSkipCallback() {
        var skipCount = 0
        composeTestRule.setContent {
            MyApplicationTheme {
                TutorialScreen3(onNext = {}, onSkip = { skipCount++ })
            }
        }

        composeTestRule.onNodeWithTag("btnSkipTutorial").performClick()

        assertEquals(1, skipCount)
    }
}
