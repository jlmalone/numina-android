# Numina Android

Native Android app for the Numina group fitness social platform.

## Status

✅ **MVP Complete** - Core features implemented and ready for testing

## Overview

Numina helps you find workout partners and discover group fitness classes. Think ClassPass meets Meetup, with a focus on community, accountability, and fitness connections (not dating).

## Features

### ✅ Implemented

- **Authentication Flow**
  - User registration with email/password
  - Secure login with JWT token management
  - Encrypted token storage using DataStore
  - Splash screen with auto-navigation

- **Profile Setup / Onboarding**
  - 5-step guided onboarding flow
  - Basic info collection (name, bio, photo)
  - Fitness interests selection (Yoga, HIIT, Spin, Pilates, Boxing, Running, Strength, Dance)
  - Fitness level slider (1-10 scale)
  - Location input (manual entry with lat/long)
  - Availability selection (days and time slots)

- **Class Discovery**
  - List view of fitness classes
  - Class filtering capabilities (date, location, type, price, intensity)
  - Offline caching with Room database
  - Pull-to-refresh functionality
  - Class cards showing: name, type, trainer, duration, location, price, intensity, spots available

- **Class Details**
  - Full class information display
  - Trainer bio and specialties
  - Location details
  - External booking via deep link
  - "Find Workout Partner" placeholder (future feature)

- **Offline Support**
  - Local caching of user profile
  - Recently viewed classes cached
  - Automatic cache cleanup (24hr TTL)

### 🚧 Coming Soon

See [TODO.md](TODO.md) for planned features:
- Partner matching algorithm
- Real-time messaging
- Group coordination
- Ratings and reviews
- Push notifications

## Technology Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit 2.9.0 + OkHttp 4.12.0
- **Local Database**: Room 2.6.1
- **Image Loading**: Coil 2.5.0
- **Navigation**: Navigation Compose 2.7.6
- **Coroutines**: Kotlinx Coroutines 1.7.3
- **Testing**: JUnit, Mockito Kotlin, Turbine

## Architecture

The app follows **Clean Architecture** principles with clear separation of concerns:

### Presentation Layer (`ui/`)
- **Compose UI**: Declarative UI with Material 3 components
- **ViewModels**: State management with StateFlow
- **Navigation**: Single-activity architecture with Navigation Compose
- **Screens**:
  - `auth/`: Splash, Login, Register
  - `onboarding/`: 5-step profile setup flow
  - `classes/`: Class list and details
  - `components/`: Reusable UI components

### Data Layer (`data/`)
- **API (`api/`)**: Retrofit interfaces for REST API
  - `AuthApi`: Login, Register
  - `UserApi`: Profile management
  - `ClassesApi`: Class discovery and details
- **Database (`db/`)**: Room database for offline caching
  - `UserEntity`: Cached user profiles
  - `FitnessClassEntity`: Cached class data
  - Type converters for complex objects
- **Repository (`repository/`)**: Single source of truth
  - `AuthRepository`: Authentication logic
  - `UserRepository`: User profile management
  - `ClassRepository`: Class data with offline support
  - `TokenManager`: Secure JWT storage
- **Models (`models/`)**: Data classes for API and domain

### Dependency Injection (`di/`)
- **NetworkModule**: Retrofit, OkHttp, API services
- **DatabaseModule**: Room database and DAOs
- Hilt modules for singleton components

## Project Structure

```
numina-android/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── kotlin/com/numina/
│       │   │   ├── MainActivity.kt
│       │   │   ├── NuminaApplication.kt
│       │   │   ├── data/
│       │   │   │   ├── api/
│       │   │   │   │   ├── AuthApi.kt
│       │   │   │   │   ├── UserApi.kt
│       │   │   │   │   └── ClassesApi.kt
│       │   │   │   ├── db/
│       │   │   │   │   ├── AppDatabase.kt
│       │   │   │   │   ├── Converters.kt
│       │   │   │   │   ├── UserDao.kt
│       │   │   │   │   ├── UserEntity.kt
│       │   │   │   │   ├── FitnessClassDao.kt
│       │   │   │   │   └── FitnessClassEntity.kt
│       │   │   │   ├── models/
│       │   │   │   │   ├── Auth.kt
│       │   │   │   │   ├── User.kt
│       │   │   │   │   └── FitnessClass.kt
│       │   │   │   └── repository/
│       │   │   │       ├── Result.kt
│       │   │   │       ├── AuthRepository.kt
│       │   │   │       ├── UserRepository.kt
│       │   │   │       ├── ClassRepository.kt
│       │   │   │       └── TokenManager.kt
│       │   │   ├── di/
│       │   │   │   ├── NetworkModule.kt
│       │   │   │   └── DatabaseModule.kt
│       │   │   └── ui/
│       │   │       ├── theme/
│       │   │       │   ├── Color.kt
│       │   │       │   ├── Theme.kt
│       │   │       │   └── Type.kt
│       │   │       ├── components/
│       │   │       │   └── CommonComponents.kt
│       │   │       ├── navigation/
│       │   │       │   ├── Screen.kt
│       │   │       │   └── NavGraph.kt
│       │   │       ├── auth/
│       │   │       │   ├── AuthViewModel.kt
│       │   │       │   ├── SplashScreen.kt
│       │   │       │   ├── LoginScreen.kt
│       │   │       │   └── RegisterScreen.kt
│       │   │       ├── onboarding/
│       │   │       │   ├── OnboardingViewModel.kt
│       │   │       │   └── OnboardingScreen.kt
│       │   │       └── classes/
│       │   │           ├── ClassesViewModel.kt
│       │   │           ├── ClassesScreen.kt
│       │   │           ├── ClassDetailsViewModel.kt
│       │   │           └── ClassDetailsScreen.kt
│       │   └── res/
│       │       ├── values/
│       │       │   ├── strings.xml
│       │       │   ├── colors.xml
│       │       │   └── themes.xml
│       │       └── xml/
│       │           ├── backup_rules.xml
│       │           └── data_extraction_rules.xml
│       └── test/
│           └── kotlin/com/numina/
│               └── ui/auth/
│                   └── AuthViewModelTest.kt
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .gitignore
├── README.md
└── TODO.md
```

## API Configuration

The app expects a backend API with the following endpoints:

### Base URLs
- **Development**: `https://dev-api.numina.app/`
- **Production**: `https://api.numina.app/`

Configure in `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "BASE_URL", "\"YOUR_API_URL\"")
```

### Endpoints

**Authentication**
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login with credentials

**User Profile**
- `GET /api/v1/users/me` - Get current user
- `PUT /api/v1/users/me` - Update profile

**Classes**
- `GET /api/v1/classes` - List classes (with filters)
- `GET /api/v1/classes/{id}` - Get class details

See API models in `data/models/` for request/response schemas.

## Quick Start

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API 35
- Gradle 8.2+

### Build and Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/numina-android.git
   cd numina-android
   ```

2. **Open in Android Studio**
   - File → Open → Select `numina-android` directory
   - Wait for Gradle sync to complete

3. **Configure API URL** (if different from default)
   - Edit `app/build.gradle.kts`
   - Update `BASE_URL` in `buildConfigField`

4. **Build the project**
   ```bash
   ./gradlew build
   ```

5. **Run on device/emulator**
   - Click "Run" in Android Studio, or:
   ```bash
   ./gradlew installDebug
   ```

### Running Tests

```bash
# Unit tests
./gradlew test

# UI tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Test coverage report
./gradlew testDebugUnitTestCoverage
```

## Design System

### Color Palette
- **Primary**: Purple (#8B5CF6) - Energetic, empowering
- **Secondary**: Teal (#14B8A6) - Fresh, active
- **Accent**: Orange (#F97316) - Motivating
- **Neutral**: Gray scale for text and backgrounds

### Typography
Material Design 3 type system with default font family:
- Display: Bold, 36-57sp (headlines)
- Title: SemiBold, 16-28sp (section headers)
- Body: Regular, 12-16sp (content)
- Label: Medium, 11-14sp (buttons, chips)

### Components
- Filled buttons for primary actions
- Outlined buttons for secondary actions
- Cards with 2dp elevation for content
- Chips for tags and filters
- Text fields with outlined style

## Security

- JWT tokens stored in encrypted DataStore
- Sensitive data excluded from backups (see `backup_rules.xml`)
- HTTPS enforced for all network requests
- ProGuard rules for release builds
- No sensitive data logged in production

## Performance

- Offline-first architecture with Room caching
- Image loading optimized with Coil
- Lazy loading for class lists
- Database queries on background threads
- Coroutines for asynchronous operations

## Accessibility

- Material 3 components with built-in accessibility
- Content descriptions on icons and images
- Touch targets meet 48dp minimum
- High contrast color ratios (WCAG AA)
- Screen reader support

## Contributing

This project was scaffolded with Claude Code Web. For contributions:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

[Add your license here]

## Acknowledgments

- Built with [Claude Code Web](https://claude.com/claude-code)
- Material Design 3 by Google
- Icons from Material Icons

---

**Numina Android** - Your fitness community companion 🏃‍♀️💪
