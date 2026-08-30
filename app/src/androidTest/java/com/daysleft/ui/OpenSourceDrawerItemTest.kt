package com.daysleft.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.daysleft.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OpenSourceDrawerItemTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun openSource_itemDisplaysInDrawer() {
        // Open navigation drawer
        composeTestRule.onNodeWithContentDescription("Open navigation drawer").performClick()

        // Verify Open Source item and GitHub description are visible
        composeTestRule.onNodeWithText("Open Source").assertIsDisplayed()
        composeTestRule.onNodeWithText("View source code on GitHub").assertIsDisplayed()
    }
}
