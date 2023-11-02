plugins {
    id("dev.chara.tasks.convention.plugin.android-library")
}

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:model"))

            implementation(project(":shared:data"))
            implementation(project(":shared:ext"))

            implementation(libs.kotlinx.datetime)

            implementation(libs.okio)

            implementation(libs.result)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "dev.chara.tasks.shared.domain"
}