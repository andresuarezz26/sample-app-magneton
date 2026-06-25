package com.example.myapplication.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.datastore.preferences.core.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.myapplication.MainActivity
import com.example.myapplication.data.local.dataStore
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class OnboardingHappyPathTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearOnboardingFlag() {
        // Reset DataStore so onboarding triggers on launch.
        runBlocking {
            InstrumentationRegistry.getInstrumentation().targetContext
                .dataStore.edit { it.clear() }
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
        // Welcome screen -> next
        composeTestRule.onNodeWithTag("btnGetStarted").performClick()

        // Select at least one field
        composeTestRule.onNodeWithText("Computer Science").performClick()
        composeTestRule.onNodeWithText("Biology").performClick()

        // Continue
        composeTestRule.onNodeWithTag("btnContinue").performClick()

        // Skip institutional email
        composeTestRule.onNodeWithTag("btnSkip").performClick()

        // Should land on Discover feed
        composeTestRule.onNodeWithTag("discoverFeed").assertIsDisplayed()
    }

    @Test
    fun secondLaunch_skipsOnboarding_goesDirectlyToDiscover() {
        // Complete onboarding first
        composeTestRule.onNodeWithTag("btnGetStarted").performClick()
        composeTestRule.onNodeWithText("Physics").performClick()
        composeTestRule.onNodeWithTag("btnContinue").performClick()
        composeTestRule.onNodeWithTag("btnSkip").performClick()
        composeTestRule.onNodeWithTag("discoverFeed").assertIsDisplayed()

        // Relaunch activity
        composeTestRule.activityRule.scenario.recreate()

        // Onboarding must NOT appear; Discover feed is shown directly.
        composeTestRule.onNodeWithTag("welcomeScreen").assertDoesNotExist()
        composeTestRule.onNodeWithTag("discoverFeed").assertIsDisplayed()
    }
}
