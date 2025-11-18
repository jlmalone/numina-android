package com.numina.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Onboarding : Screen("onboarding")
    object Classes : Screen("classes")
    object ClassDetails : Screen("class_details/{classId}") {
        fun createRoute(classId: String) = "class_details/$classId"
    }
    object Groups : Screen("groups")
    object GroupDetail : Screen("group_detail/{groupId}") {
        fun createRoute(groupId: String) = "group_detail/$groupId"
    }
    object CreateGroup : Screen("create_group")
    object GroupMembers : Screen("group_members/{groupId}") {
        fun createRoute(groupId: String) = "group_members/$groupId"
    }
    object GroupActivity : Screen("group_activity/{groupId}/{activityId}") {
        fun createRoute(groupId: String, activityId: String) = "group_activity/$groupId/$activityId"
    }
    object CreateActivity : Screen("create_activity/{groupId}") {
        fun createRoute(groupId: String) = "create_activity/$groupId"
    }
}
