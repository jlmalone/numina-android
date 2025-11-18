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
