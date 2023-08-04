@file:Suppress("UNUSED_VARIABLE")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.atomicfu)

    alias(libs.plugins.android.library)

    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    jvmToolchain(17)

    android()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
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
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.androidx.activity)

                implementation(libs.koin.android)
            }
        }
        val androidUnitTest by getting

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation(libs.nserrorkt)
            }
        }

        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting

        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

compose {
    kotlinCompilerPlugin.set(dependencies.compiler.auto)
}

android {
    namespace = "dev.chara.tasks.shared.ui"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    sourceSets["main"].res.srcDirs("src/androidMain/res")
}