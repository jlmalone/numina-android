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
    object ReviewsList : Screen("reviews/{classId}") {
        fun createRoute(classId: String) = "reviews/$classId"
    }
    object WriteReview : Screen("write_review/{classId}") {
        fun createRoute(classId: String) = "write_review/$classId"
    }
    object EditReview : Screen("edit_review/{reviewId}") {
        fun createRoute(reviewId: String) = "edit_review/$reviewId"
    }
    object MyReviews : Screen("my_reviews")
    object PendingReviews : Screen("pending_reviews")

    // Social screens
    object Feed : Screen("feed")
    object DiscoverUsers : Screen("discover_users")
    object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: String) = "user_profile/$userId"
    }
    object Following : Screen("following")
    object ActivityDetail : Screen("activity_detail/{activityId}") {
        fun createRoute(activityId: String) = "activity_detail/$activityId"
    }

    // Messages screens
    object Messages : Screen("messages")
    object Chat : Screen("chat/{userId}") {
        fun createRoute(userId: String) = "chat/$userId"
    }
    object NewChat : Screen("new_chat")

    // Groups screens
    object Groups : Screen("groups")
    object GroupDetail : Screen("group_detail/{groupId}") {
        fun createRoute(groupId: String) = "group_detail/$groupId"
    }
    object CreateGroup : Screen("create_group")
    object GroupActivity : Screen("group_activity/{groupId}") {
        fun createRoute(groupId: String) = "group_activity/$groupId"
    }
    object GroupMembers : Screen("group_members/{groupId}") {
        fun createRoute(groupId: String) = "group_members/$groupId"
    }
    object CreateActivity : Screen("create_activity/{groupId}") {
        fun createRoute(groupId: String) = "create_activity/$groupId"
    }

    // Notifications screens
    object Notifications : Screen("notifications")
    object NotificationPreferences : Screen("notification_preferences")
}
