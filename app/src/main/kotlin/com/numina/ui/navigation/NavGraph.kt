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
import com.numina.ui.groups.*
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

        composable(Screen.Groups.route) {
            val groupsViewModel: GroupsViewModel = hiltViewModel()
            val groupsUiState by groupsViewModel.uiState.collectAsState()

            GroupsScreen(
                uiState = groupsUiState,
                onGroupClick = { groupId ->
                    navController.navigate(Screen.GroupDetail.createRoute(groupId))
                },
                onCreateGroup = {
                    navController.navigate(Screen.CreateGroup.route)
                },
                onRefresh = {
                    groupsViewModel.loadGroups(refresh = true)
                },
                onFilterClick = {
                    // Future feature: open filter dialog
                },
                onToggleMyGroups = { showMyGroups ->
                    groupsViewModel.toggleShowMyGroups(showMyGroups)
                }
            )
        }

        composable(
            route = Screen.GroupDetail.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) {
            val groupDetailViewModel: GroupDetailViewModel = hiltViewModel()
            val groupDetailUiState by groupDetailViewModel.uiState.collectAsState()

            GroupDetailScreen(
                uiState = groupDetailUiState,
                onBack = {
                    navController.popBackStack()
                },
                onJoinGroup = {
                    groupDetailViewModel.joinGroup()
                },
                onLeaveGroup = {
                    groupDetailViewModel.leaveGroup()
                },
                onActivityClick = { activityId ->
                    val groupId = groupDetailUiState.group?.id ?: return@GroupDetailScreen
                    navController.navigate(Screen.GroupActivity.createRoute(groupId, activityId))
                },
                onCreateActivity = {
                    val groupId = groupDetailUiState.group?.id ?: return@GroupDetailScreen
                    navController.navigate(Screen.CreateActivity.createRoute(groupId))
                },
                onViewMembers = {
                    val groupId = groupDetailUiState.group?.id ?: return@GroupDetailScreen
                    navController.navigate(Screen.GroupMembers.createRoute(groupId))
                },
                onRsvp = { activityId, status ->
                    groupDetailViewModel.rsvpToActivity(activityId, status)
                },
                onRetry = {
                    groupDetailViewModel.loadGroupDetails()
                }
            )
        }

        composable(Screen.CreateGroup.route) {
            val createGroupViewModel: CreateGroupViewModel = hiltViewModel()
            val createGroupUiState by createGroupViewModel.uiState.collectAsState()

            CreateGroupScreen(
                uiState = createGroupUiState,
                onUpdateName = createGroupViewModel::updateName,
                onUpdateDescription = createGroupViewModel::updateDescription,
                onUpdateCategory = createGroupViewModel::updateCategory,
                onUpdatePrivacy = createGroupViewModel::updatePrivacy,
                onUpdateCity = createGroupViewModel::updateCity,
                onUpdateState = createGroupViewModel::updateState,
                onUpdateCountry = createGroupViewModel::updateCountry,
                onUpdateMaxMembers = createGroupViewModel::updateMaxMembers,
                onUpdatePhotoUrl = createGroupViewModel::updatePhotoUrl,
                onNext = createGroupViewModel::nextStep,
                onBack = createGroupViewModel::previousStep,
                onCreate = createGroupViewModel::createGroup,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToGroup = { groupId ->
                    navController.navigate(Screen.GroupDetail.createRoute(groupId)) {
                        popUpTo(Screen.Groups.route)
                    }
                }
            )
        }

        composable(
            route = Screen.GroupMembers.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) {
            val groupDetailViewModel: GroupDetailViewModel = hiltViewModel()
            val groupDetailUiState by groupDetailViewModel.uiState.collectAsState()

            GroupMembersScreen(
                members = groupDetailUiState.members,
                isLoading = groupDetailUiState.isLoading,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.GroupActivity.route,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType },
                navArgument("activityId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val groupDetailViewModel: GroupDetailViewModel = hiltViewModel()
            val groupDetailUiState by groupDetailViewModel.uiState.collectAsState()
            val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
            val activity = groupDetailUiState.activities.find { it.id == activityId }

            activity?.let {
                GroupActivityScreen(
                    activity = it,
                    isMember = groupDetailUiState.group?.isMember ?: false,
                    onBack = {
                        navController.popBackStack()
                    },
                    onRsvp = { status ->
                        groupDetailViewModel.rsvpToActivity(activityId, status)
                    }
                )
            }
        }

        composable(
            route = Screen.CreateActivity.route,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) {
            val createActivityViewModel: CreateActivityViewModel = hiltViewModel()
            val createActivityUiState by createActivityViewModel.uiState.collectAsState()

            CreateActivityScreen(
                uiState = createActivityUiState,
                onUpdateTitle = createActivityViewModel::updateTitle,
                onUpdateDescription = createActivityViewModel::updateDescription,
                onUpdateDateTime = createActivityViewModel::updateDateTime,
                onUpdateLocation = createActivityViewModel::updateLocation,
                onUpdateFitnessClassId = createActivityViewModel::updateFitnessClassId,
                onCreate = createActivityViewModel::createActivity,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToActivity = { activityId ->
                    val groupId = it.arguments?.getString("groupId") ?: return@CreateActivityScreen
                    navController.navigate(Screen.GroupActivity.createRoute(groupId, activityId)) {
                        popUpTo(Screen.GroupDetail.createRoute(groupId))
                    }
                }
            )
        }
    }
}
