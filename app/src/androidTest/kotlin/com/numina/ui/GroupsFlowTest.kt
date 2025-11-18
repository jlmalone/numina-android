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
class GroupsFlowTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        loginTestUser()
    }

    @Test
    fun createGroupFlow() {
        composeTestRule.onNodeWithTag("bottomNav:groups").performClick()
        composeTestRule.onNodeWithTag("createGroupButton").performClick()

        // Fill form
        composeTestRule.onNodeWithTag("groupNameField")
            .performTextInput("Test Yoga Group")
        composeTestRule.onNodeWithTag("groupDescriptionField")
            .performTextInput("A group for yoga enthusiasts")

        // Select category
        composeTestRule.onNodeWithText("Yoga").performClick()

        // Submit
        composeTestRule.onNodeWithText("Create Group").performClick()

        // Verify navigation to group details
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Test Yoga Group")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun joinGroupFlow() {
        composeTestRule.onNodeWithTag("bottomNav:groups").performClick()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("groupCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click on a group
        composeTestRule.onAllNodesWithTag("groupCard")[0].performClick()

        // Join group
        composeTestRule.onNodeWithText("Join Group").performClick()

        // Verify membership
        composeTestRule.onNodeWithText("Leave Group").assertIsDisplayed()
    }

    private fun loginTestUser() {
        // Helper
    }
}
