# Task: Add Testing & Production Build Setup

> **IMPORTANT**: Check for `.task-testing-production-completed` before starting.
> If it exists, respond: "✅ This task has already been implemented."
> **When finished**, create `.task-testing-production-completed` file.

## Overview
Add comprehensive UI testing, production build configuration, and Play Store preparation for the Numina Android app.

## Requirements

### 1. UI Testing Suite (Espresso)

Update `app/build.gradle.kts`:
```kotlin
dependencies {
    // Existing dependencies...

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.6")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.48")
}
```

#### Test Infrastructure
**File**: `app/src/androidTest/kotlin/com/numina/TestRunner.kt`
```kotlin
package com.numina

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class TestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
```

Update `app/build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        testInstrumentationRunner = "com.numina.TestRunner"
    }
}
```

#### Test Files
**File**: `app/src/androidTest/kotlin/com/numina/ui/AuthFlowTest.kt`
```kotlin
package com.numina.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.numina.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AuthFlowTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun completeLoginFlow() {
        // Navigate to login
        composeTestRule.onNodeWithText("Login").performClick()

        // Enter credentials
        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("test@example.com")

        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("test123")

        // Submit
        composeTestRule.onNodeWithText("Sign In").performClick()

        // Verify navigation to home
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun registrationFlow() {
        composeTestRule.onNodeWithText("Register").performClick()

        composeTestRule.onNodeWithTag("nameField")
            .performTextInput("Test User")
        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("newuser@example.com")
        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("password123")

        composeTestRule.onNodeWithText("Create Account").performClick()

        // Verify success
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Welcome")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }
}
```

**File**: `app/src/androidTest/kotlin/com/numina/ui/ClassesFlowTest.kt`
```kotlin
package com.numina.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.numina.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ClassesFlowTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        // Login first
        loginTestUser()
    }

    @Test
    fun searchAndViewClassDetails() {
        // Navigate to classes
        composeTestRule.onNodeWithTag("bottomNav:classes").performClick()

        // Search
        composeTestRule.onNodeWithTag("searchField")
            .performTextInput("yoga")

        // Wait for results
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("classCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click first result
        composeTestRule.onAllNodesWithTag("classCard")[0].performClick()

        // Verify details screen
        composeTestRule.onNodeWithTag("classDetails").assertIsDisplayed()
    }

    @Test
    fun bookmarkClass() {
        composeTestRule.onNodeWithTag("bottomNav:classes").performClick()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("classCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Bookmark first class
        composeTestRule.onAllNodesWithTag("bookmarkButton")[0].performClick()

        // Verify toast or snackbar
        composeTestRule.onNodeWithText("Bookmarked").assertIsDisplayed()
    }

    private fun loginTestUser() {
        // Helper to login before tests
    }
}
```

**File**: `app/src/androidTest/kotlin/com/numina/ui/GroupsFlowTest.kt`
```kotlin
package com.numina.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.numina.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class GroupsFlowTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        loginTestUser()
    }

    @Test
    fun createGroupFlow() {
        composeTestRule.onNodeWithTag("bottomNav:groups").performClick()
        composeTestRule.onNodeWithTag("createGroupButton").performClick()

        // Fill form
        composeTestRule.onNodeWithTag("groupNameField")
            .performTextInput("Test Yoga Group")
        composeTestRule.onNodeWithTag("groupDescriptionField")
            .performTextInput("A group for yoga enthusiasts")

        // Select category
        composeTestRule.onNodeWithText("Yoga").performClick()

        // Submit
        composeTestRule.onNodeWithText("Create Group").performClick()

        // Verify navigation to group details
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Test Yoga Group")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun joinGroupFlow() {
        composeTestRule.onNodeWithTag("bottomNav:groups").performClick()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("groupCard")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click on a group
        composeTestRule.onAllNodesWithTag("groupCard")[0].performClick()

        // Join group
        composeTestRule.onNodeWithText("Join Group").performClick()

        // Verify membership
        composeTestRule.onNodeWithText("Leave Group").assertIsDisplayed()
    }

    private fun loginTestUser() {
        // Helper
    }
}
```

**File**: `app/src/androidTest/kotlin/com/numina/ui/MessagingFlowTest.kt`
```kotlin
package com.numina.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.numina.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MessagingFlowTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        loginTestUser()
    }

    @Test
    fun sendMessageFlow() {
        composeTestRule.onNodeWithTag("bottomNav:messages").performClick()

        // Click on conversation
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("conversationItem")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithTag("conversationItem")[0].performClick()

        // Send message
        composeTestRule.onNodeWithTag("messageInput")
            .performTextInput("Test message")
        composeTestRule.onNodeWithTag("sendButton").performClick()

        // Verify message appears
        composeTestRule.onNodeWithText("Test message").assertIsDisplayed()
    }

    private fun loginTestUser() {
        // Helper
    }
}
```

### 2. Production Build Configuration

**File**: `app/build.gradle.kts` - Update with:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            buildConfigField("String", "API_BASE_URL", "\"https://api.numina.app\"")
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080\"")
        }
    }

    bundle {
        language {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
}
```

**File**: `app/proguard-rules.pro` - Add:
```proguard
# Numina specific rules
-keep class com.numina.data.** { *; }
-keep class com.numina.domain.model.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }

# Gson/Kotlinx Serialization
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
```

### 3. Play Store Configuration

**File**: `app/src/main/play/release-notes/en-US/default.txt`
```
New in this version:
- Find and book fitness classes nearby
- Connect with other fitness enthusiasts
- Join fitness groups and activities
- Real-time messaging
- Track your fitness journey
```

**File**: `app/src/main/play/listings/en-US/full-description.txt`
```
Numina - Your Fitness Social Network

Discover, book, and attend fitness classes in your area while connecting with a community of like-minded fitness enthusiasts.

FEATURES:
• Browse thousands of fitness classes from top providers
• Get personalized class recommendations based on your interests
• Book classes directly through provider platforms
• Join fitness groups and coordinate activities
• Real-time messaging with other members
• Track your bookings and attendance
• Share your fitness journey and achievements
• Receive push notifications for group activities and messages

SUPPORTED ACTIVITIES:
Yoga, CrossFit, Running, Cycling, Dance, Martial Arts, Swimming, Hiking, Climbing, and more!

Whether you're a beginner or an experienced athlete, Numina helps you find the perfect class and the right community to support your fitness goals.

Download Numina today and start your fitness journey!
```

**File**: `app/src/main/play/listings/en-US/short-description.txt`
```
Discover fitness classes and connect with your fitness community
```

**File**: `app/src/main/play/listings/en-US/title.txt`
```
Numina - Fitness Classes & Social Network
```

### 4. Build Scripts

**File**: `scripts/build-release.sh`
```bash
#!/bin/bash
set -e

echo "🏗️  Building Numina Android Release..."

# Check environment variables
if [ -z "$KEYSTORE_FILE" ] || [ -z "$KEYSTORE_PASSWORD" ]; then
    echo "❌ Error: Keystore environment variables not set"
    echo "Required: KEYSTORE_FILE, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD"
    exit 1
fi

# Clean
echo "🧹 Cleaning..."
./gradlew clean

# Run tests
echo "🧪 Running tests..."
./gradlew test

# Build release AAB
echo "📦 Building release bundle..."
./gradlew bundleRelease

# Build release APK (optional)
echo "📦 Building release APK..."
./gradlew assembleRelease

echo "✅ Build complete!"
echo "📱 AAB: app/build/outputs/bundle/release/app-release.aab"
echo "📱 APK: app/build/outputs/apk/release/app-release.apk"
```

**File**: `scripts/run-ui-tests.sh`
```bash
#!/bin/bash
set -e

echo "🧪 Running Numina Android UI Tests..."

# Check for connected device/emulator
if ! adb devices | grep -q "device$"; then
    echo "❌ No device/emulator found"
    echo "Please connect a device or start an emulator"
    exit 1
fi

# Run instrumented tests
echo "▶️  Running instrumented tests..."
./gradlew connectedAndroidTest

echo "✅ Tests complete!"
echo "📊 Report: app/build/reports/androidTests/connected/index.html"
```

### 5. CI Configuration

**File**: `.github/workflows/android-ci.yml`
```yaml
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: gradle

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Run unit tests
      run: ./gradlew test

    - name: Build debug APK
      run: ./gradlew assembleDebug

    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk

  instrumented-tests:
    runs-on: macos-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: gradle

    - name: Run instrumented tests
      uses: reactivecircus/android-emulator-runner@v2
      with:
        api-level: 34
        arch: x86_64
        script: ./gradlew connectedAndroidTest

    - name: Upload test reports
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: test-reports
        path: app/build/reports/androidTests/
```

### 6. Documentation

**File**: `RELEASE.md`
```markdown
# Numina Android - Release Process

## Prerequisites
- Android Studio Giraffe or later
- JDK 17
- Google Play Console access
- Release keystore

## Build Release

1. **Set environment variables**:
   ```bash
   export KEYSTORE_FILE=/path/to/release.keystore
   export KEYSTORE_PASSWORD=your_password
   export KEY_ALIAS=your_alias
   export KEY_PASSWORD=your_key_password
   ```

2. **Build**:
   ```bash
   ./scripts/build-release.sh
   ```

3. **Test APK**:
   ```bash
   adb install app/build/outputs/apk/release/app-release.apk
   ```

## Play Store Submission

1. **Generate AAB**:
   ```bash
   ./gradlew bundleRelease
   ```

2. **Upload to Play Console**:
   - Go to https://play.google.com/console
   - Select Numina app
   - Navigate to Release > Production
   - Create new release
   - Upload `app/build/outputs/bundle/release/app-release.aab`
   - Fill in release notes
   - Submit for review

## Testing

### Unit Tests
```bash
./gradlew test
```

### UI Tests
```bash
./scripts/run-ui-tests.sh
```

## Versioning
Update version in `app/build.gradle.kts`:
```kotlin
versionCode = 1  // Increment for each release
versionName = "1.0.0"  // Semantic versioning
```

## Checklist
- [ ] All tests passing
- [ ] Version code incremented
- [ ] Release notes updated
- [ ] Screenshots updated in Play Console
- [ ] Privacy policy link valid
- [ ] Terms of service link valid
- [ ] Feature graphic updated
- [ ] Promotional assets uploaded
```

## Completion Checklist
- [ ] UI test suite created with Espresso
- [ ] Production build configuration complete
- [ ] ProGuard rules added
- [ ] Play Store assets created
- [ ] Build scripts working
- [ ] CI/CD pipeline configured
- [ ] Documentation complete
- [ ] `.task-testing-production-completed` file created

## Testing
Run unit tests:
```bash
./gradlew test
```

Run UI tests:
```bash
./scripts/run-ui-tests.sh
```

Build release:
```bash
./scripts/build-release.sh
```

## Success Criteria
1. ✅ Comprehensive UI test coverage for main flows
2. ✅ Release build configuration secure and optimized
3. ✅ Play Store assets complete and professional
4. ✅ CI/CD pipeline functional
5. ✅ Documentation clear and comprehensive
