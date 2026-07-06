package com.example.myapplication.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myapplication.MainActivity
import com.example.myapplication.data.onboarding.OnboardingPreferences
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingHappyPathTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearOnboardingFlag() {
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            OnboardingPreferences(context).clear()
        }
    }

    @Test
    fun firstLaunch_showsWelcomeScreen() {
        composeTestRule.onNodeWithTag("welcomeScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("welcomeHeadline").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btnGetStarted").assertIsDisplayed()
    }

    @Test
    fun onboarding_selectInterestsAndComplete_landsOnDiscover() {
        composeTestRule.onNodeWithTag("btnGetStarted").performClick()

        composeTestRule.onNodeWithText("Computer Science").performClick()
        composeTestRule.onNodeWithText("Biology").performClick()

        composeTestRule.onNodeWithTag("btnContinue").performClick()

        composeTestRule.onNodeWithTag("btnSkip").performClick()

        composeTestRule.onNodeWithTag("discoverFeed").assertIsDisplayed()
    }

    @Test
    fun secondLaunch_skipsOnboarding_goesDirectlyToDiscover() {
        composeTestRule.onNodeWithTag("btnGetStarted").performClick()
        composeTestRule.onNodeWithText("Physics").performClick()
        composeTestRule.onNodeWithTag("btnContinue").performClick()
        composeTestRule.onNodeWithTag("btnSkip").performClick()

        composeTestRule.activityRule.scenario.recreate()

        assertTrue(
            composeTestRule.onAllNodesWithTag("welcomeScreen")
                .fetchSemanticsNodes(atLeastOneRootRequired = false)
                .isEmpty()
        )
        composeTestRule.onNodeWithTag("discoverFeed").assertIsDisplayed()
    }
}
