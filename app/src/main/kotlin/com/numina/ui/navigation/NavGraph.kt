package com.numina.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.numina.ui.auth.AuthViewModel
import com.numina.ui.auth.LoginScreen
import com.numina.ui.auth.RegisterScreen
import com.numina.ui.auth.SplashScreen
import com.numina.ui.classes.ClassDetailsScreen
import com.numina.ui.classes.ClassDetailsViewModel
import com.numina.ui.classes.ClassesScreen
import com.numina.ui.classes.ClassesViewModel
import com.numina.ui.notifications.NotificationPreferencesScreen
import com.numina.ui.notifications.NotificationPreferencesViewModel
import com.numina.ui.notifications.NotificationsScreen
import com.numina.ui.notifications.NotificationsViewModel
import com.numina.ui.onboarding.OnboardingScreen
import com.numina.ui.onboarding.OnboardingViewModel
import com.numina.ui.reviews.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authUiState by authViewModel.uiState.collectAsState()

            SplashScreen(
                isAuthenticated = authUiState.isAuthenticated,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToClasses = {
                    navController.navigate(Screen.Classes.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authUiState by authViewModel.uiState.collectAsState()

            LoginScreen(
                uiState = authUiState,
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToClasses = {
                    navController.navigate(Screen.Classes.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authUiState by authViewModel.uiState.collectAsState()

            RegisterScreen(
                uiState = authUiState,
                onRegister = { email, password, name ->
                    authViewModel.register(email, password, name)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            val onboardingViewModel: OnboardingViewModel = hiltViewModel()
            val onboardingState by onboardingViewModel.uiState.collectAsState()

            OnboardingScreen(
                uiState = onboardingState,
                onUpdateName = onboardingViewModel::updateName,
                onUpdateBio = onboardingViewModel::updateBio,
                onToggleInterest = onboardingViewModel::toggleInterest,
                onUpdateFitnessLevel = onboardingViewModel::updateFitnessLevel,
                onUpdateLocation = onboardingViewModel::updateLocation,
                onUpdateAvailability = onboardingViewModel::updateAvailability,
                onNext = onboardingViewModel::nextStep,
                onBack = onboardingViewModel::previousStep,
                onComplete = onboardingViewModel::completeOnboarding,
                onNavigateToClasses = {
                    navController.navigate(Screen.Classes.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Classes.route) {
            val classesViewModel: ClassesViewModel = hiltViewModel()
            val classesUiState by classesViewModel.uiState.collectAsState()

            ClassesScreen(
                uiState = classesUiState,
                onClassClick = { classId ->
                    navController.navigate(Screen.ClassDetails.createRoute(classId))
                },
                onRefresh = {
                    classesViewModel.loadClasses(refresh = true)
                },
                onFilterClick = {
                    // Future feature: open filter dialog
                }
            )
        }

        composable(
            route = Screen.ClassDetails.route,
            arguments = listOf(navArgument("classId") { type = NavType.StringType })
        ) {
            val classDetailsViewModel: ClassDetailsViewModel = hiltViewModel()
            val classDetailsUiState by classDetailsViewModel.uiState.collectAsState()

            ClassDetailsScreen(
                uiState = classDetailsUiState,
                onBack = {
                    navController.popBackStack()
                },
                onRetry = {
                    classDetailsViewModel.loadClassDetails()
                },
                onViewReviews = { classId ->
                    navController.navigate(Screen.ReviewsList.createRoute(classId))
                }
            )
        }

        composable(
            route = Screen.ReviewsList.route,
            arguments = listOf(navArgument("classId") { type = NavType.StringType })
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId") ?: return@composable

            ReviewsListScreen(
                classId = classId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onWriteReview = { classId ->
                    navController.navigate(Screen.WriteReview.createRoute(classId))
                }
            )
        }

        composable(
            route = Screen.WriteReview.route,
            arguments = listOf(navArgument("classId") { type = NavType.StringType })
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId") ?: return@composable

            WriteReviewScreen(
                classId = classId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditReview.route,
            arguments = listOf(navArgument("reviewId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getString("reviewId") ?: return@composable

            // Note: In a real app, you'd fetch the review by ID and pass it to the screen
            WriteReviewScreen(
                classId = null,
                existingReview = null, // This would be fetched
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.MyReviews.route) {
            MyReviewsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditReview = { reviewId ->
                    navController.navigate(Screen.EditReview.createRoute(reviewId))
                },
                onClassClick = { classId ->
                    navController.navigate(Screen.ClassDetails.createRoute(classId))
                }
            )
        }

        composable(Screen.PendingReviews.route) {
            PendingReviewsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onWriteReview = { classId ->
                    navController.navigate(Screen.WriteReview.createRoute(classId))
                }
            )
        }

        // Social screens
        composable(Screen.Feed.route) {
            val feedViewModel: FeedViewModel = hiltViewModel()
            val feedUiState by feedViewModel.uiState.collectAsState()

            FeedScreen(
                uiState = feedUiState,
                onRefresh = {
                    feedViewModel.loadFeed(refresh = true)
                },
                onLoadMore = {
                    feedViewModel.loadMoreActivities()
                },
                onLikeClick = { activityId ->
                    feedViewModel.likeActivity(activityId)
                },
                onUnlikeClick = { activityId ->
                    feedViewModel.unlikeActivity(activityId)
                },
                onCommentClick = { activityId ->
                    navController.navigate(Screen.ActivityDetail.createRoute(activityId))
                },
                onUserClick = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                }
            )
        }

        composable(Screen.DiscoverUsers.route) {
            val discoverViewModel: DiscoverViewModel = hiltViewModel()
            val discoverUiState by discoverViewModel.uiState.collectAsState()

            DiscoverUsersScreen(
                uiState = discoverUiState,
                onUserClick = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                },
                onFollowClick = { userId, _ ->
                    // Handle follow/unfollow through repository
                },
                onSearchQueryChange = { query ->
                    discoverViewModel.updateSearchQuery(query)
                },
                onApplyFilters = {
                    discoverViewModel.applyFilters()
                },
                onLoadMore = {
                    discoverViewModel.loadMoreUsers()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            val userProfileViewModel: UserProfileViewModel = hiltViewModel()
            val userProfileUiState by userProfileViewModel.uiState.collectAsState()

            UserProfileScreen(
                uiState = userProfileUiState,
                onBack = {
                    navController.popBackStack()
                },
                onFollowToggle = {
                    userProfileViewModel.toggleFollow()
                },
                onActivityClick = { activityId ->
                    navController.navigate(Screen.ActivityDetail.createRoute(activityId))
                },
                onFollowersClick = {
                    navController.navigate(Screen.Following.route)
                },
                onFollowingClick = {
                    navController.navigate(Screen.Following.route)
                },
                onLoadMoreActivities = {
                    userProfileViewModel.loadMoreActivities()
                }
            )
        }

        composable(Screen.Following.route) {
            val followingViewModel: FollowingViewModel = hiltViewModel()
            val followingUiState by followingViewModel.uiState.collectAsState()

            FollowingScreen(
                uiState = followingUiState,
                onUserClick = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                },
                onFollowClick = { userId, _ ->
                    followingViewModel.followUser(userId)
                },
                onUnfollowClick = { userId ->
                    followingViewModel.unfollowUser(userId)
                },
                onTabChange = { tab ->
                    followingViewModel.selectTab(tab)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ActivityDetail.route,
            arguments = listOf(navArgument("activityId") { type = NavType.StringType })
        ) {
            val activityDetailViewModel: ActivityDetailViewModel = hiltViewModel()
            val activityDetailUiState by activityDetailViewModel.uiState.collectAsState()

            ActivityDetailScreen(
                uiState = activityDetailUiState,
                onBack = {
                    navController.popBackStack()
                },
                onLikeToggle = {
                    val activity = activityDetailUiState.activity
                    if (activity != null) {
                        if (activity.isLiked) {
                            activityDetailViewModel.unlikeActivity()
                        } else {
                            activityDetailViewModel.likeActivity()
                        }
                    }
                },
                onCommentTextChange = { text ->
                    activityDetailViewModel.updateCommentText(text)
                },
                onPostComment = {
                    activityDetailViewModel.postComment()
                }
            )
        }
    }
}
