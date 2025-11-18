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
