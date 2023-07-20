import java.util.Properties

val localProperties = Properties()
val propertiesFile: File = project.rootProject.file("local.properties")
if (propertiesFile.exists()) {
    localProperties.load(propertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.android.application)

    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)

    id("kotlin-parcelize")
}

kotlin {
    jvmToolchain(8)
}

android {
    namespace = "dev.chara.tasks.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "dev.chara.tasks.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 13
        versionName = "0.7"
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("debug")
        }
    }
    flavorDimensions += "endpoint"
    productFlavors {
        create("prod") {
            dimension = "endpoint"

            buildConfigField("String", "ENDPOINT_URL", "\"https://tasks-api.chara.dev\"")
        }
        create("dev") {
            dimension = "endpoint"

            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            buildConfigField(
                "String",
                "ENDPOINT_URL",
                "\"${localProperties.getProperty("tasks.endpoint_url")}\""
            )
            resValue("string", "app_name", "Tasks (Dev)")
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(project(":shared"))

    implementation(libs.napier)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.compose.material3)

    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.material3)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.work)

    implementation(libs.navigation.reimagined)

    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.compose.reorderable)

    implementation(libs.coil.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}