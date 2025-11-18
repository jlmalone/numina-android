package com.numina.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.numina.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ClassesFlowTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        // Login first
        loginTestUser()
    }

    @Test
    fun searchAndViewClassDetails() {
        // Navigate to classes
        composeTestRule.onNodeWithTag("bottomNav:classes").performClick()

        // Search
        composeTestRule.onNodeWithTag("searchField")
            .performTextInput("yoga")

        // Wait for results
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("classCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click first result
        composeTestRule.onAllNodesWithTag("classCard")[0].performClick()

        // Verify details screen
        composeTestRule.onNodeWithTag("classDetails").assertIsDisplayed()
    }

    @Test
    fun bookmarkClass() {
        composeTestRule.onNodeWithTag("bottomNav:classes").performClick()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("classCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Bookmark first class
        composeTestRule.onAllNodesWithTag("bookmarkButton")[0].performClick()

        // Verify toast or snackbar
        composeTestRule.onNodeWithText("Bookmarked").assertIsDisplayed()
    }

    private fun loginTestUser() {
        // Helper to login before tests
    }
}
