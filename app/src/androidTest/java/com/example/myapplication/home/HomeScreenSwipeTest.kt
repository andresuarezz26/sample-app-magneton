package com.example.myapplication.home

import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
    fun openHomeScreen_waitTenSeconds_swipeUp_succeeds() {
        composeTestRule.waitForIdle()

        Thread.sleep(10_000)

        composeTestRule.onRoot().performTouchInput { swipeUp() }
        composeTestRule.waitForIdle()
    }
}
