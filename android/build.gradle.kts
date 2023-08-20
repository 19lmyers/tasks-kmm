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

    alias(libs.plugins.jetbrains.compose)

    id("kotlin-parcelize")

    alias(libs.plugins.ktfmt)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "dev.chara.tasks.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "dev.chara.tasks.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 16
        versionName = "0.8.2"
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        buildConfig = true
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
            isDefault = true

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

    implementation(project(":shared:model"))
    implementation(project(":shared:database"))
    implementation(project(":shared:data"))
    implementation(project(":shared:domain"))
    implementation(project(":shared:component"))
    implementation(project(":shared:ui"))
    implementation(project(":shared:ext"))

    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.androidx.activity)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.material3)

    implementation(libs.androidx.lifecycle.service)

    implementation(libs.androidx.work)

    implementation(compose.foundation)
    implementation(compose.material3)

    implementation(libs.decompose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.kermit)
    implementation(libs.kermit.crashlytics)

    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.kotlinx.datetime)

    implementation(libs.materialkolor)

    implementation(libs.result)
}

compose {
    kotlinCompilerPlugin.set(dependencies.compiler.auto)
}

ktfmt {
    kotlinLangStyle()
}