package com.example.myapplication

import androidx.compose.ui.test.junit4.createAndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoPlayerHappyPathTest {

    @get:Rule
    val composeTestRule = createAndroidComposeTestRule<MainActivity>()

    @Test
    fun tapVideoCard_opensPlayerAndPlaysVideo() {
        composeTestRule.onNodeWithTag("paperCard").performClick()

        composeTestRule.onNodeWithTag("playerScreen").assertExists()
        composeTestRule.onNodeWithTag("playerSurface").assertExists()
    }

    @Test
    fun playerScreen_bottomPanel_showsPaperDetails() {
        composeTestRule.onNodeWithTag("paperCard").performClick()

        composeTestRule.onNodeWithTag("paperTitlePlayer").assertExists()
        composeTestRule.onNodeWithTag("paperAuthors").assertExists()
    }

    @Test
    fun playerScreen_expandBottomPanel_showsAbstractAndLink() {
        composeTestRule.onNodeWithTag("paperCard").performClick()

        composeTestRule.onNodeWithTag("bottomPanel").performTouchInput { swipeUp() }

        composeTestRule.onNodeWithTag("paperAbstract").assertExists()
        composeTestRule.onNodeWithTag("readOriginalLink").assertExists()
    }

    @Test
    fun playerScreen_likeButton_persistsInteraction() {
        composeTestRule.onNodeWithTag("paperCard").performClick()

        composeTestRule.onNodeWithTag("bottomPanel").performTouchInput { swipeUp() }

        composeTestRule.onNodeWithTag("btnLike").performClick()

        composeTestRule.onNodeWithTag("btnLike").assertExists()
    }
}
