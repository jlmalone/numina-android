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
    object Bookings : Screen("bookings")
    object BookingDetail : Screen("booking_detail/{bookingId}") {
        fun createRoute(bookingId: String) = "booking_detail/$bookingId"
    }
    object Calendar : Screen("calendar")
    object ReminderPreferences : Screen("reminder_preferences")
    object AttendanceStats : Screen("attendance_stats")
}
