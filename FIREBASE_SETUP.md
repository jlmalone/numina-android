# Firebase Cloud Messaging Setup

This app uses Firebase Cloud Messaging (FCM) for push notifications. To enable FCM:

## Setup Steps

1. **Create a Firebase Project**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or select an existing one
   - Add an Android app to your Firebase project

2. **Register Your App**
   - Package name: `com.numina`
   - App nickname: Numina Android
   - SHA-1 certificate fingerprint (optional for FCM)

3. **Download Configuration File**
   - Download the `google-services.json` file from Firebase Console
   - Replace the placeholder file at `app/google-services.json` with the real file
   - **Important**: Never commit the real `google-services.json` to version control

4. **Enable FCM**
   - In Firebase Console, go to Project Settings > Cloud Messaging
   - Note your Server Key for backend integration

5. **Backend Integration**
   The backend needs to:
   - Accept FCM token registration at `POST /api/v1/notifications/register-device`
   - Send notifications using Firebase Admin SDK or FCM HTTP API
   - Store user FCM tokens in the database

## Notification Payload Format

The app expects notifications in this format:

```json
{
  "notification": {
    "title": "New Match!",
    "body": "You matched with Sarah for yoga class"
  },
  "data": {
    "type": "match",
    "match_id": "12345",
    "user_id": "67890"
  }
}
```

### Notification Types
- `message` - New direct messages
- `match` - New workout buddy matches
- `group` - Group activity updates
- `reminder` - Class/workout reminders

## Testing Notifications

### Using Firebase Console
1. Go to Firebase Console > Cloud Messaging
2. Click "Send your first message"
3. Enter title and body
4. Select target: Single device (use FCM token from app logs)
5. Add custom data fields as needed

### Using cURL
```bash
curl -X POST https://fcm.googleapis.com/fcm/send \
  -H "Authorization: key=YOUR_SERVER_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "DEVICE_FCM_TOKEN",
    "notification": {
      "title": "Test Notification",
      "body": "This is a test"
    },
    "data": {
      "type": "message"
    }
  }'
```

## Troubleshooting

**Build fails with "File google-services.json is missing"**
- Ensure `google-services.json` exists in `app/` directory
- The placeholder file should work for builds, but FCM won't function

**Notifications not received**
- Check FCM token is registered with backend
- Verify app has notification permissions (Android 13+)
- Check notification preferences in app settings
- Review FCM service logs: `adb logcat | grep FCMService`

**Token not registering**
- Ensure backend endpoint `/api/v1/notifications/register-device` is working
- Check network connectivity
- Review repository logs for registration errors

## Security Notes

- Keep your `google-services.json` file secure
- Add it to `.gitignore` to prevent accidental commits
- Use environment-specific configuration files for dev/staging/prod
- Rotate Firebase server keys periodically
