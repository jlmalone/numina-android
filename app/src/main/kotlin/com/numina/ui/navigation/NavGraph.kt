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
                }
            )
        }

        composable(Screen.Notifications.route) {
            val notificationsViewModel: NotificationsViewModel = hiltViewModel()
            val notificationsUiState by notificationsViewModel.uiState.collectAsState()

            NotificationsScreen(
                uiState = notificationsUiState,
                onNotificationClick = { notificationId ->
                    notificationsViewModel.markAsRead(notificationId)
                    // Future: navigate based on notification type
                },
                onRefresh = {
                    notificationsViewModel.loadNotifications(refresh = true)
                },
                onSettingsClick = {
                    navController.navigate(Screen.NotificationPreferences.route)
                },
                onMarkAllRead = {
                    notificationsViewModel.markAllAsRead()
                }
            )
        }

        composable(Screen.NotificationPreferences.route) {
            val preferencesViewModel: NotificationPreferencesViewModel = hiltViewModel()
            val preferencesUiState by preferencesViewModel.uiState.collectAsState()

            NotificationPreferencesScreen(
                uiState = preferencesUiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onToggleMessages = preferencesViewModel::updateMessagesEnabled,
                onToggleMatches = preferencesViewModel::updateMatchesEnabled,
                onToggleGroups = preferencesViewModel::updateGroupsEnabled,
                onToggleReminders = preferencesViewModel::updateRemindersEnabled,
                onToggleEmailFallback = preferencesViewModel::updateEmailFallbackEnabled,
                onSave = {
                    preferencesViewModel.savePreferences()
                }
            )
        }
    }
}
