# TASK: Android UI Polish & Improvements

> **IMPORTANT**: Check for `.task-polish-completed` before starting.
> **When finished**, create `.task-polish-completed` file.

## 🎯 OBJECTIVE

Polish the Android app with better loading states, error handling, and UX improvements.

## 📋 REQUIREMENTS

### 1. Skeleton Loading States

**Replace generic loading spinners with skeleton screens**:
- Class list: Show skeleton class cards
- Messages: Show skeleton conversation items
- Groups: Show skeleton group cards
- Feed: Show skeleton activity items
- Profile: Show skeleton profile layout

**Implementation**:
- Create `SkeletonLoader.kt` composable
- Add shimmer animation effect
- Use for all data-loading screens

### 2. Improved Error Handling

**Better error UI**:
- Replace generic error text with rich error cards
- Add retry buttons to all error states
- Show specific error messages (network, auth, server)
- Add error illustrations (optional)

**Create**:
- `ErrorView.kt` composable with retry callback
- `NetworkErrorView.kt` for offline state
- `EmptyStateView.kt` for empty lists

### 3. Pull-to-Refresh Everywhere

**Add pull-to-refresh to**:
- Class list
- Messages/conversations
- Groups list
- Activity feed
- Bookings list
- My reviews

**Use**: Accompanist SwipeRefresh or Material3 PullRefreshIndicator

### 4. Smooth Animations

**Add animations**:
- Screen transitions (slide in/out)
- List item animations (fade in)
- Button press feedback (scale down)
- Loading state transitions
- Success/error state transitions

**Use**: Jetpack Compose Animation APIs

### 5. Better Input Validation

**Improve forms**:
- Real-time validation feedback
- Show error messages inline
- Disable submit buttons when invalid
- Show character counts for text fields
- Clear validation errors on input

**Forms to improve**:
- Login/register
- Write review
- Create group
- Message compose
- Edit profile

### 6. Haptic Feedback

**Add vibration on**:
- Button presses (light)
- Pull-to-refresh (medium)
- Errors (heavy)
- Success actions (light)

**Use**: `LocalHapticFeedback.current.performHapticFeedback()`

### 7. Image Loading Optimization

**Improvements**:
- Add placeholder images
- Add loading shimmer for images
- Error fallback images
- Proper image caching (Coil defaults should handle this)
- Compress large uploads

### 8. Accessibility

**Add**:
- Content descriptions for all images/icons
- Semantic labels for buttons
- Proper heading hierarchy
- Touch target sizes (min 48dp)
- Color contrast checks

### 9. Offline Indicators

**Add**:
- Network status banner at top of screen
- Show "Offline" badge on cached data
- Disable actions that require network
- Queue actions for when back online

### 10. Polish Details

**Misc improvements**:
- Add badges (unread counts, new items)
- Add tooltips for first-time users
- Improve spacing/padding consistency
- Add dividers where needed
- Improve typography hierarchy
- Add status bar color theming

## ✅ ACCEPTANCE CRITERIA

- [ ] All screens have skeleton loading states
- [ ] All screens have proper error handling with retry
- [ ] Pull-to-refresh works on all list screens
- [ ] Smooth animations throughout app
- [ ] Form validation is real-time and clear
- [ ] Haptic feedback on key interactions
- [ ] Images load smoothly with placeholders
- [ ] Accessibility labels added everywhere
- [ ] Offline state handled gracefully
- [ ] App feels polished and professional

## 📝 DELIVERABLES

- Skeleton loader components
- Error view components
- Pull-to-refresh integration
- Animation implementations
- Form validation improvements
- Haptic feedback integration
- Accessibility labels
- Offline handling
- Tests

## 🚀 COMPLETION

1. Build: `./gradlew build`
2. Test thoroughly on emulator
3. Create `.task-polish-completed`
4. Commit: "Polish Android UI (loading states, errors, animations)"
5. Push: `git push -u origin claude/polish-android-ui`

---

**Est. Time**: 60-75 min | **Priority**: MEDIUM-HIGH
