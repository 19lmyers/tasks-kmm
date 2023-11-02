plugins {
    id("dev.chara.tasks.convention.plugin.android-library")

    alias(libs.plugins.kotlin.atomicfu)

    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:model"))
            implementation(project(":shared:domain"))
            implementation(project(":shared:component"))
            implementation(project(":shared:ext"))

            implementation(libs.atomicfu)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)

            implementation(libs.decompose)
            implementation(libs.decompose.extensions)

            implementation(libs.kamel)

            implementation(libs.kermit)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.kotlinx.datetime)

            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.materialkolor)

            implementation(libs.result)
            implementation(libs.result.coroutines)

            implementation(libs.window.size)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.core)
            implementation(libs.androidx.activity)

            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            implementation(libs.nserrorkt)
        }
    }
}

compose {
    kotlinCompilerPlugin.set(dependencies.compiler.auto)
}

android {
    namespace = "dev.chara.tasks.shared.ui"
    sourceSets["main"].res.srcDirs("src/androidMain/res")
}