# TASK: Add Groups UI to Numina Android App

> **IMPORTANT**: Check for `.task-groups-ui-completed` before starting.
> **When finished**, create `.task-groups-ui-completed` file.

## 🎯 OBJECTIVE

Build group discovery, creation, and management interfaces.

## 📋 REQUIREMENTS

### Features
1. **Groups Discovery**
   - Browse public groups
   - Filter by category, location, size
   - Search groups
   - Recommended groups based on interests

2. **Group Details**
   - Group info (name, description, photo, members)
   - Member list
   - Upcoming activities
   - Join/leave actions
   - Invite members

3. **Group Creation**
   - Multi-step form (name, category, privacy, location, photo)
   - Set max members
   - Create initial activity

4. **Group Activities**
   - List group activities
   - Create new activity
   - Link to fitness class
   - RSVP (going/maybe/not going)
   - Activity details screen

5. **My Groups**
   - Groups I've joined
   - Groups I own/admin

### Screens
- `GroupsScreen.kt` - Browse/discover groups
- `GroupDetailScreen.kt` - Group details and activities
- `CreateGroupScreen.kt` - Create new group
- `GroupMembersScreen.kt` - Member list
- `GroupActivityScreen.kt` - Activity details
- `CreateActivityScreen.kt` - Schedule group activity

### ViewModels
- `GroupsViewModel.kt` - Browse, join/leave
- `GroupDetailViewModel.kt` - Group details, activities
- `CreateGroupViewModel.kt` - Group creation

### API Integration
- `GET /api/v1/groups` - List/search groups
- `POST /api/v1/groups` - Create group
- `POST /api/v1/groups/{id}/join` - Join group
- `GET /api/v1/groups/{id}/activities` - Activities
- `POST /api/v1/groups/{id}/activities/{aid}/rsvp` - RSVP

### Local Caching
- Cache groups in Room
- Cache group activities
- Offline viewing

## ✅ ACCEPTANCE CRITERIA

- [ ] Users can browse and discover groups
- [ ] Group creation flow works
- [ ] Join/leave functionality works
- [ ] Activity creation and RSVP functional
- [ ] Member management works
- [ ] Material Design 3 styling
- [ ] Offline caching implemented

## 📝 DELIVERABLES

- Group screens and components
- ViewModels
- Room entities
- Repository layer
- Navigation
- Tests

## 🚀 COMPLETION

1. Build: `./gradlew build`
2. Test
3. Create `.task-groups-ui-completed`
4. Commit: "Add groups UI with discovery and management"
5. Push: `git push -u origin claude/add-groups-ui`

---

**Est. Time**: 75-90 min | **Priority**: HIGH
