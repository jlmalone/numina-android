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
