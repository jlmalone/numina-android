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
class MessagingFlowTest {
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
    fun sendMessageFlow() {
        composeTestRule.onNodeWithTag("bottomNav:messages").performClick()

        // Click on conversation
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("conversationItem")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithTag("conversationItem")[0].performClick()

        // Send message
        composeTestRule.onNodeWithTag("messageInput")
            .performTextInput("Test message")
        composeTestRule.onNodeWithTag("sendButton").performClick()

        // Verify message appears
        composeTestRule.onNodeWithText("Test message").assertIsDisplayed()
    }

    private fun loginTestUser() {
        // Helper
    }
}
