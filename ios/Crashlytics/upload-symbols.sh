#!/bin/sh

# Get source directory
SOURCE_ROOT=$(pwd)

# Get build directory
BUILD_ROOT=$(xcodebuild -project ./ios/Tasks.xcodeproj -showBuildSettings | grep -m 1 'BUILD_ROOT' | grep -o '\/.*$')

# Navigate to Xcode project directory
cd "$BUILD_ROOT" || exit
cd "../../" || exit

CRASHLYTICS_SYMBOL_UPLOADER="./SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/upload-symbols"

GOOGLE_SERVICES_PLIST="$SOURCE_ROOT/ios/Tasks/GoogleService-Info.plist"
DSYM_FOLDER_PATH="$SOURCE_ROOT/build/ios"

exec "$CRASHLYTICS_SYMBOL_UPLOADER" -gsp "$GOOGLE_SERVICES_PLIST" -p ios "$DSYM_FOLDER_PATH"