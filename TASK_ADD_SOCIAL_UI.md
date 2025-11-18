# TASK: Add Social Features UI to Numina Android App

> **IMPORTANT**: Check for `.task-social-ui-completed` before starting.
> **When finished**, create `.task-social-ui-completed` file.

## 🎯 OBJECTIVE

Build social networking features: following, activity feed, user discovery.

## 📋 REQUIREMENTS

### Features
1. **Activity Feed**
   - Home feed showing followed users' activities
   - Activity types: workouts, groups joined, reviews, milestones
   - Like and comment on activities
   - Pull-to-refresh
   - Infinite scroll pagination

2. **User Discovery**
   - Discover users page
   - Search by name, location, interests
   - Filter by fitness level, activity level
   - Suggested users based on matching algorithm
   - View user profiles

3. **Following System**
   - Follow/unfollow buttons
   - Followers list
   - Following list
   - Mutual connections
   - Follow suggestions

4. **User Profiles (Public View)**
   - View other users' profiles
   - See fitness interests, level, location
   - View public activity history
   - Mutual groups/connections
   - Follow status

5. **Social Stats**
   - Follower/following counts
   - Activity count
   - Workout stats

### Screens
- `FeedScreen.kt` - Activity feed (home tab)
- `DiscoverUsersScreen.kt` - User discovery
- `UserProfileScreen.kt` - Public profile view
- `FollowersScreen.kt` - Followers list
- `FollowingScreen.kt` - Following list
- `ActivityDetailScreen.kt` - Activity detail with comments

### Components
- `ActivityFeedItem.kt` - Feed item UI
- `UserListItem.kt` - User list item
- `FollowButton.kt` - Follow/unfollow button
- `LikeCommentBar.kt` - Like/comment actions
- `StatsCard.kt` - User stats display

### ViewModels
- `FeedViewModel.kt` - Activity feed logic
- `DiscoverViewModel.kt` - User discovery
- `UserProfileViewModel.kt` - Public profile
- `FollowingViewModel.kt` - Following management

### API Integration
- `GET /api/v1/social/feed` - Activity feed
- `POST /api/v1/social/follow/{userId}` - Follow user
- `DELETE /api/v1/social/unfollow/{userId}` - Unfollow
- `GET /api/v1/social/discover-users` - Discover users
- `GET /api/v1/social/users/{id}/profile` - View profile
- `POST /api/v1/social/activity/{id}/like` - Like activity
- `POST /api/v1/social/activity/{id}/comment` - Comment

### Local Caching
- Cache feed items
- Cache user profiles
- Following/followers lists

## ✅ ACCEPTANCE CRITERIA

- [ ] Activity feed displays and updates
- [ ] Follow/unfollow works correctly
- [ ] User discovery with search and filters
- [ ] Like and comment functionality
- [ ] Public profiles viewable
- [ ] Stats accurate
- [ ] Pagination works smoothly
- [ ] Material Design 3 styling

## 📝 DELIVERABLES

- Social feature screens
- ViewModels and repositories
- Room entities
- Navigation
- Tests

## 🚀 COMPLETION

1. Build: `./gradlew build`
2. Test
3. Create `.task-social-ui-completed`
4. Commit: "Add social features with feed and discovery"
5. Push: `git push -u origin claude/add-social-ui`

---

**Est. Time**: 75-90 min | **Priority**: MEDIUM-HIGH
