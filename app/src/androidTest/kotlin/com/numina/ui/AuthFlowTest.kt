package com.numina.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.numina.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AuthFlowTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun completeLoginFlow() {
        // Navigate to login
        composeTestRule.onNodeWithText("Login").performClick()

        // Enter credentials
        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("test@example.com")

        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("test123")

        // Submit
        composeTestRule.onNodeWithText("Sign In").performClick()

        // Verify navigation to home
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun registrationFlow() {
        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.onNodeWithTag("nameField")
            .performTextInput("Test User")
        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("newuser@example.com")
        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("password123")

        composeTestRule.onNodeWithText("Create Account").performClick()

        // Verify success
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Welcome")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }
}
