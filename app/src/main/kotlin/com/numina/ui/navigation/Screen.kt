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
    object Messages : Screen("messages")
    object Chat : Screen("chat/{conversationId}/{participantId}/{participantName}?participantAvatar={participantAvatar}") {
        fun createRoute(
            conversationId: String,
            participantId: String,
            participantName: String,
            participantAvatar: String? = null
        ) = "chat/$conversationId/$participantId/$participantName" +
                if (participantAvatar != null) "?participantAvatar=$participantAvatar" else ""
    }
    object NewChat : Screen("new_chat")
}
