# TASK: Add Messaging UI to Numina Android App

> **IMPORTANT**: Check for `.task-messaging-ui-completed` before starting.
> **When finished**, create `.task-messaging-ui-completed` file.

## 🎯 OBJECTIVE

Build a complete messaging interface with real-time chat using Jetpack Compose.

## 📋 REQUIREMENTS

### Features
1. **Conversations List**
   - List all conversations with preview
   - Unread count badges
   - Avatar, last message, timestamp
   - Swipe to delete/archive
   - Pull-to-refresh

2. **Chat Interface**
   - Real-time messaging via WebSocket
   - Message bubbles (sent/received styling)
   - Timestamps
   - Read receipts
   - Typing indicators
   - Auto-scroll to latest
   - Image attachments (optional)

3. **User Search**
   - Find users to start new conversations
   - Search by name, fitness interests
   - Recently matched users quick access

4. **Notifications**
   - In-app new message alerts
   - Badge count on tab icon

### Screens to Build
- `MessagesScreen.kt` - Conversations list
- `ChatScreen.kt` - Individual chat
- `NewChatScreen.kt` - Start new conversation
- `MessageComposeBar.kt` - Message input component
- `MessageBubble.kt` - Message UI component
- `ConversationItem.kt` - List item component

### ViewModels
- `MessagesViewModel.kt` - Manage conversations list
- `ChatViewModel.kt` - Handle WebSocket, send/receive messages

### API Integration
Connect to backend messaging endpoints:
- `GET /api/v1/messages/conversations`
- `GET /api/v1/messages/conversations/{id}`
- `POST /api/v1/messages/send`
- `WS /api/v1/ws/messages`

### Local Caching
- Cache conversations and messages in Room
- Offline message queue (send when back online)
- Sync on app launch

## ✅ ACCEPTANCE CRITERIA

- [ ] Conversations list displays correctly
- [ ] Real-time messaging works via WebSocket
- [ ] Messages persist locally in Room
- [ ] Typing indicators visible
- [ ] Unread counts accurate
- [ ] New conversation flow works
- [ ] All screens follow Material Design 3
- [ ] Dark mode supported

## 📝 DELIVERABLES

- Messaging screens and components
- ViewModels with WebSocket integration
- Room entities for messages/conversations
- Repository layer
- Navigation integration
- Tests

## 🚀 COMPLETION

1. Build: `./gradlew build`
2. Test on emulator
3. Create `.task-messaging-ui-completed`
4. Commit: "Add messaging UI with real-time chat"
5. Push: `git push -u origin claude/add-messaging-ui`

---

**Est. Time**: 75-90 min | **Priority**: HIGH
