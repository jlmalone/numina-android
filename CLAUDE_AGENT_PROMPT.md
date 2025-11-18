# 🤖 CLAUDE CODE WEB AGENT PROMPT

> **IMPORTANT**: Before starting, check if `.agent-completed` file exists in the root directory.
> If it exists, respond: "✅ This task has already been completed. See README.md for details."
> **When finished**, create `.agent-completed` file with timestamp and summary.

---

# TASK: Build Numina Android App Prototype

## Repository Purpose
You are working on `numina-android`, the native Android app for Numina - a group fitness social platform. Users create rich profiles, discover fitness classes, match with workout partners, and coordinate meetups.

## Current State
Repository initialized with basic README. Starting from scratch.

## Your Task
Create a modern Android app with Jetpack Compose UI, following Material Design 3 principles and Clean Architecture patterns.

### Core Requirements

1. **Project Structure**
   - Kotlin with Jetpack Compose
   - Android API 26+ (target API 35)
   - Hilt for dependency injection
   - Retrofit for API communication
   - Room for local caching
   - Coil for image loading
   - Jetpack Navigation Compose

2. **Feature Modules**

   **A. Authentication Flow**
   - Splash screen
   - Login screen (email + password)
   - Registration screen (email, password, name)
   - JWT token storage (encrypted DataStore)

   **B. Profile Setup Flow**
   - Multi-step onboarding:
     1. Basic info (name, bio, photo)
     2. Fitness preferences (interests: yoga, HIIT, spin, etc.)
     3. Fitness level (slider 1-10)
     4. Location (use device location + manual override)
     5. Availability (select preferred days/times)
   - Save to backend via API

   **C. Class Discovery Screen**
   - List of fitness classes (LazyColumn)
   - Filters: date range, location radius, class type, price range
   - Each class card shows: name, time, location, trainer, intensity, price
   - Tap to view class details
   - "Find Partner" button → future feature
   - Pull-to-refresh

   **D. Class Details Screen**
   - Full class information
   - Trainer bio
   - Location map (static map image for now)
   - "Book on [Provider]" button → open external URL
   - "Find Workout Partner" button → future feature

3. **Backend Integration**
   - Base API URL configurable (for dev/prod)
   - Retrofit client with JWT authentication interceptor
   - API endpoints:
     - POST `/api/v1/auth/register`
     - POST `/api/v1/auth/login`
     - GET/PUT `/api/v1/users/me`
     - GET `/api/v1/classes` (with query params)
     - GET `/api/v1/classes/{id}`

4. **Local Database (Room)**
   - Cache user profile locally
   - Cache recently viewed classes
   - Offline-first approach where possible

5. **UI/UX Design**
   - Material Design 3 with dynamic color theming
   - Fitness-focused color scheme (energetic, empowering)
   - NOT romantic/dating app aesthetics - focus on community/fitness
   - Smooth animations and transitions
   - Loading states, error states, empty states

### Technical Constraints

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Min SDK**: 26, Target SDK: 35
- **DI**: Hilt
- **Networking**: Retrofit + OkHttp
- **Local DB**: Room
- **Image Loading**: Coil
- **Navigation**: Navigation Compose
- **State**: ViewModel + StateFlow

### File Structure
```
numina-android/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── kotlin/com/numina/
│       │   ├── NuminaApplication.kt
│       │   ├── di/                 # Hilt modules
│       │   ├── data/
│       │   │   ├── api/            # Retrofit interfaces
│       │   │   ├── db/             # Room database
│       │   │   ├── repository/     # Repository pattern
│       │   │   └── models/         # Data models
│       │   ├── domain/             # Use cases
│       │   └── ui/
│       │       ├── auth/           # Login/Register screens
│       │       ├── onboarding/     # Profile setup flow
│       │       ├── classes/        # Class list & details
│       │       ├── profile/        # User profile screen
│       │       └── components/     # Reusable Compose components
│       └── res/
│           ├── values/
│           │   ├── strings.xml
│           │   └── themes.xml
│           └── drawable/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

### Acceptance Criteria

1. ✅ User can register and login successfully
2. ✅ JWT token stored securely and used for authenticated requests
3. ✅ Complete onboarding flow with all profile fields
4. ✅ Classes screen displays list of fitness classes from backend
5. ✅ Filters work correctly (date, location, type, price)
6. ✅ Class details screen shows full information
7. ✅ External booking link opens in browser
8. ✅ Offline caching for recently viewed content
9. ✅ Proper error handling and user feedback
10. ✅ Material Design 3 with modern, fitness-focused aesthetics
11. ✅ App builds and runs on Android 8.0+ devices
12. ✅ Unit tests for ViewModels and repositories

### Deliverables

- Complete Android app with all features above
- Clean Architecture implementation
- Hilt DI setup
- Room database for caching
- `.gitignore` file (exclude .gradle/, .idea/, build/, local.properties, *.apk, *.keystore)
- README with:
  - Setup instructions
  - Architecture overview
  - API configuration
  - Build and run guide
  - Screenshots (or ASCII art mockups)
- TODO.md with next features: matching, messaging, ratings

### How to Report Back

1. **Update README.md** with:
   - Quick start guide (clone, build, run)
   - Architecture explanation (layers, modules, patterns)
   - API configuration instructions
   - Feature list with completion status
   - UI screenshots or mockup descriptions
   - Testing instructions
   - Next steps and roadmap

2. **Create TODO.md** with prioritized features for the next phase

3. **Create `.agent-completed` file** with content:
   ```
   Completed: [timestamp]
   Summary: Numina Android app scaffolded successfully
   Features: Auth, Onboarding, Class Discovery
   Status: All acceptance criteria met
   Build: Successful (APK generated)
   Next: See TODO.md
   ```

4. **Commit and push** all changes with message:
   ```
   feat: Complete Android app with auth and class discovery

   🤖 Generated with [Claude Code](https://claude.com/claude-code)

   Co-Authored-By: Claude <noreply@anthropic.com>
   ```
