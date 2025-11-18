package com.numina.ui.social

import com.numina.data.models.*
import com.numina.data.repository.Result
import com.numina.data.repository.SocialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private lateinit var socialRepository: SocialRepository
    private lateinit var viewModel: FeedViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        socialRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        // Given
        whenever(socialRepository.getCachedActivities()).thenReturn(flowOf(emptyList()))
        whenever(socialRepository.fetchActivityFeed(any(), any())).thenReturn(
            flow {
                emit(Result.Loading)
                emit(Result.Success(ActivityFeedResponse(emptyList(), 1, 1, false)))
            }
        )

        // When
        viewModel = FeedViewModel(socialRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(emptyList(), state.activities)
        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `loadFeed should update state correctly on success`() = runTest {
        // Given
        val mockActivities = listOf(
            Activity(
                id = "1",
                userId = "user1",
                userName = "John Doe",
                userPhotoUrl = null,
                type = ActivityType.WORKOUT,
                title = "Morning Run",
                description = "5K run",
                createdAt = "2024-01-01T10:00:00Z",
                likesCount = 10,
                commentsCount = 5,
                isLiked = false,
                metadata = null
            )
        )
        val mockResponse = ActivityFeedResponse(mockActivities, 1, 1, false)

        whenever(socialRepository.getCachedActivities()).thenReturn(flowOf(emptyList()))
        whenever(socialRepository.fetchActivityFeed(any(), any())).thenReturn(
            flow {
                emit(Result.Loading)
                emit(Result.Success(mockResponse))
            }
        )

        // When
        viewModel = FeedViewModel(socialRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(mockActivities, state.activities)
        assertFalse(state.isLoading)
        assertFalse(state.hasMore)
    }

    @Test
    fun `likeActivity should update activity like status`() = runTest {
        // Given
        val mockActivity = Activity(
            id = "1",
            userId = "user1",
            userName = "John Doe",
            userPhotoUrl = null,
            type = ActivityType.WORKOUT,
            title = "Morning Run",
            description = "5K run",
            createdAt = "2024-01-01T10:00:00Z",
            likesCount = 10,
            commentsCount = 5,
            isLiked = false,
            metadata = null
        )
        val mockResponse = ActivityFeedResponse(listOf(mockActivity), 1, 1, false)

        whenever(socialRepository.getCachedActivities()).thenReturn(flowOf(emptyList()))
        whenever(socialRepository.fetchActivityFeed(any(), any())).thenReturn(
            flow {
                emit(Result.Loading)
                emit(Result.Success(mockResponse))
            }
        )
        whenever(socialRepository.likeActivity("1")).thenReturn(
            flow {
                emit(Result.Success(mockActivity.copy(isLiked = true, likesCount = 11)))
            }
        )

        // When
        viewModel = FeedViewModel(socialRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.likeActivity("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        val likedActivity = state.activities.find { it.id == "1" }
        assertTrue(likedActivity?.isLiked ?: false)
        assertEquals(11, likedActivity?.likesCount)
    }
}
