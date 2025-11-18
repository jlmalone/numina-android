package com.numina.ui.auth

import com.numina.data.models.AuthResponse
import com.numina.data.models.User
import com.numina.data.repository.AuthRepository
import com.numina.data.repository.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        whenever(authRepository.isLoggedIn()).thenReturn(false)
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success updates uiState with authenticated user`() = runTest {
        // Given
        val testUser = User(
            id = "1",
            email = "test@example.com",
            name = "Test User"
        )
        val authResponse = AuthResponse(token = "test_token", user = testUser)
        whenever(authRepository.login("test@example.com", "password"))
            .thenReturn(flowOf(Result.Success(authResponse)))

        // When
        viewModel.login("test@example.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.isAuthenticated)
        assertEquals(testUser, uiState.user)
        assertFalse(uiState.isLoading)
        assertEquals(null, uiState.error)
    }

    @Test
    fun `login failure updates uiState with error message`() = runTest {
        // Given
        val errorMessage = "Invalid credentials"
        whenever(authRepository.login("test@example.com", "wrong_password"))
            .thenReturn(flowOf(Result.Error(errorMessage)))

        // When
        viewModel.login("test@example.com", "wrong_password")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isAuthenticated)
        assertEquals(null, uiState.user)
        assertFalse(uiState.isLoading)
        assertEquals(errorMessage, uiState.error)
    }

    @Test
    fun `register success updates uiState with authenticated user`() = runTest {
        // Given
        val testUser = User(
            id = "1",
            email = "new@example.com",
            name = "New User"
        )
        val authResponse = AuthResponse(token = "test_token", user = testUser)
        whenever(authRepository.register("new@example.com", "password", "New User"))
            .thenReturn(flowOf(Result.Success(authResponse)))

        // When
        viewModel.register("new@example.com", "password", "New User")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.isAuthenticated)
        assertEquals(testUser, uiState.user)
        assertFalse(uiState.isLoading)
        assertEquals(null, uiState.error)
    }
}
