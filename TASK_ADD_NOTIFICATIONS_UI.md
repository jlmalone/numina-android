# TASK: Add Push Notifications to Numina Android App

> **IMPORTANT**: Check for `.task-notifications-ui-completed` before starting.
> **When finished**, create `.task-notifications-ui-completed` file.

## 🎯 OBJECTIVE

Implement FCM push notifications with in-app notification center.

## 📋 REQUIREMENTS

### Features
1. **FCM Integration**
   - Firebase Cloud Messaging setup
   - Register device token with backend
   - Handle incoming notifications
   - Deep linking from notifications

2. **Notification Center**
   - In-app notification list
   - Notification types: messages, matches, groups, reminders
   - Mark as read
   - Clear all
   - Unread count badge

3. **Notification Preferences**
   - Settings screen for notification toggles
   - Per-type preferences (messages, matches, groups, etc.)
   - Quiet hours configuration
   - Email fallback toggle

4. **Notification Handling**
   - Foreground notifications (in-app snackbar)
   - Background notifications (system tray)
   - Notification click navigation
   - Grouped notifications

### Implementation Files
- `FirebaseMessagingService.kt` - FCM service
- `NotificationsScreen.kt` - Notification center UI
- `NotificationPreferencesScreen.kt` - Settings
- `NotificationItem.kt` - List item component
- `NotificationsViewModel.kt` - State management

### API Integration
- `POST /api/v1/notifications/register-device` - Register FCM token
- `GET /api/v1/notifications/history` - Notification history
- `POST /api/v1/notifications/{id}/mark-read` - Mark read
- `GET /api/v1/notifications/preferences` - Get preferences
- `PUT /api/v1/notifications/preferences` - Update preferences

### Local Storage
- Cache notifications in Room
- Store preferences locally
- FCM token persistence

### Firebase Setup
- Add `google-services.json`
- Configure FCM in `build.gradle`
- Request notification permissions (Android 13+)

## ✅ ACCEPTANCE CRITERIA

- [ ] FCM receives push notifications
- [ ] Notifications display in system tray
- [ ] In-app notification center works
- [ ] Deep linking navigates correctly
- [ ] Preferences control notification delivery
- [ ] Unread badge count accurate
- [ ] Android 13+ permission flow works
- [ ] Notification icons and styling correct

## 📝 DELIVERABLES

- FCM service implementation
- Notification screens
- ViewModel and repository
- Room entities
- Notification permission handling
- Firebase config
- Tests

## 🚀 COMPLETION

1. Build: `./gradlew build`
2. Test notifications on device
3. Create `.task-notifications-ui-completed`
4. Commit: "Add push notifications with FCM"
5. Push: `git push -u origin claude/add-notifications-ui`

---

**Est. Time**: 60-75 min | **Priority**: HIGH
