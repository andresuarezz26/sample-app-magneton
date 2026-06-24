package com.example.myapplication.discover

import androidx.compose.ui.test.junit4.createAndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiscoverHappyPathTest {

    @get:Rule
    val composeTestRule = createAndroidComposeTestRule<MainActivity>()

    @Test
    fun discoverFeed_loadsAndDisplaysContent() {
        composeTestRule.apply {
            onNodeWithText("Discover").performClick()

            waitUntil(timeoutMillis = 5000) {
                onAllNodes(isDisplayed()).filter { node ->
                    node.getTextAsString().contains("Quantum") ||
                    node.getTextAsString().contains("CRISPR") ||
                    node.getTextAsString().contains("Machine Learning")
                }.fetchSemanticsNodes().isNotEmpty()
            }

            onNodeWithText("Quantum Entanglement: The EPR Paradox Revisited").assertExists()
        }
    }

    @Test
    fun discoverFeed_filterByCategory_showsFilteredResults() {
        composeTestRule.apply {
            onNodeWithText("Discover").performClick()

            waitUntil(timeoutMillis = 5000) {
                onAllNodes(isDisplayed()).filter { node ->
                    node.getTextAsString().contains("Physics")
                }.fetchSemanticsNodes().isNotEmpty()
            }

            onNodeWithText("Physics").performClick()

            waitUntil(timeoutMillis = 5000) {
                onAllNodes(isDisplayed()).filter { node ->
                    node.getTextAsString().contains("Quantum") ||
                    node.getTextAsString().contains("Superconductivity")
                }.fetchSemanticsNodes().isNotEmpty()
            }

            onNodeWithText("Quantum Entanglement: The EPR Paradox Revisited").assertExists()
        }
    }

    @Test
    fun discoverFeed_displaysPaperDetails() {
        composeTestRule.apply {
            onNodeWithText("Discover").performClick()

            waitUntil(timeoutMillis = 5000) {
                onAllNodes(isDisplayed()).filter { node ->
                    node.getTextAsString().contains("Alice Smith") ||
                    node.getTextAsString().contains("Quantum")
                }.fetchSemanticsNodes().isNotEmpty()
            }

            onNodeWithText("Quantum Entanglement: The EPR Paradox Revisited").assertExists()
            onNodeWithText("Alice Smith").assertExists()
            onNodeWithText("Physics").assertExists()
        }
    }
}
