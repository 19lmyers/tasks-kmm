@file:Suppress("UNUSED_VARIABLE")

plugins {
    alias(libs.plugins.kotlin.multiplatform)

    alias(libs.plugins.android.library)

    alias(libs.plugins.ktfmt)
}

kotlin {
    jvmToolchain(17)

    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:model"))

                implementation(project(":shared:data"))
                implementation(project(":shared:ext"))

                implementation(libs.kotlinx.datetime)

                implementation(libs.okio)

                implementation(libs.result)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting
        val androidUnitTest by getting

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
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

android {
    namespace = "dev.chara.tasks.shared.domain"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

ktfmt {
    kotlinLangStyle()
}