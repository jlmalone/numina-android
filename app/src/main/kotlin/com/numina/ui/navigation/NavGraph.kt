package com.numina.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.numina.ui.onboarding.OnboardingScreen
import com.numina.ui.onboarding.OnboardingViewModel
import com.numina.ui.messages.MessagesScreen
import com.numina.ui.messages.MessagesViewModel
import com.numina.ui.messages.ChatScreen
import com.numina.ui.messages.ChatViewModel
import com.numina.ui.messages.NewChatScreen
import com.numina.ui.messages.NewChatViewModel
import com.numina.data.repository.TokenManager
import kotlinx.coroutines.launch

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

        composable(Screen.Messages.route) {
            val messagesViewModel: MessagesViewModel = hiltViewModel()
            val messagesUiState by messagesViewModel.uiState.collectAsState()

            MessagesScreen(
                uiState = messagesUiState,
                onConversationClick = { conversationId, participantId, participantName, participantAvatar ->
                    navController.navigate(
                        Screen.Chat.createRoute(
                            conversationId = conversationId,
                            participantId = participantId,
                            participantName = participantName,
                            participantAvatar = participantAvatar
                        )
                    )
                },
                onNewChatClick = {
                    navController.navigate(Screen.NewChat.route)
                },
                onRefresh = {
                    messagesViewModel.loadConversations(refresh = true)
                }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("participantId") { type = NavType.StringType },
                navArgument("participantName") { type = NavType.StringType },
                navArgument("participantAvatar") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            val chatViewModel: ChatViewModel = hiltViewModel()
            val chatUiState by chatViewModel.uiState.collectAsState()
            val tokenManager: TokenManager = hiltViewModel()

            ChatScreen(
                uiState = chatUiState,
                onSendMessage = chatViewModel::sendMessage,
                onTyping = chatViewModel::onTyping,
                onBack = {
                    navController.popBackStack()
                },
                currentUserId = tokenManager.getUserId() ?: ""
            )
        }

        composable(Screen.NewChat.route) {
            val newChatViewModel: NewChatViewModel = hiltViewModel()
            val newChatUiState by newChatViewModel.uiState.collectAsState()
            val scope = rememberCoroutineScope()

            NewChatScreen(
                users = newChatUiState.users,
                isLoading = newChatUiState.isLoading,
                onUserSelect = { user ->
                    scope.launch {
                        val conversationId = newChatViewModel.createConversation(user)
                        navController.navigate(
                            Screen.Chat.createRoute(
                                conversationId = conversationId,
                                participantId = user.id,
                                participantName = user.name,
                                participantAvatar = user.profilePicture
                            )
                        ) {
                            popUpTo(Screen.Messages.route)
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                },
                onSearch = newChatViewModel::searchUsers
            )
        }
    }
}
