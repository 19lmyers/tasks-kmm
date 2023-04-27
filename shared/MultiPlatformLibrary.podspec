Pod::Spec.new do |spec|
    spec.name                     = 'MultiPlatformLibrary'
    spec.version                  = '1.0'
    spec.homepage                 = 'https://tasks.chara.dev/'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Shared components for Tasks'
    spec.vendored_frameworks      = 'build/cocoapods/framework/MultiPlatformLibrary.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '16.0'
    spec.osx.deployment_target = '13.3'
    spec.dependency 'FirebaseAnalytics'
    spec.dependency 'FirebaseCore'
    spec.dependency 'FirebaseCrashlytics'
    spec.dependency 'FirebaseMessaging'
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':shared',
        'PRODUCT_MODULE_NAME' => 'MultiPlatformLibrary',
    }
                
    spec.script_phases = [
        {
            :name => 'Build MultiPlatformLibrary',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end