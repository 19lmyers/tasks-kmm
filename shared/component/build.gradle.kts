plugins {
    id("dev.chara.tasks.convention.plugin.android-library")

    id("kotlin-parcelize")
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
            implementation(project(":shared:domain"))

            implementation(libs.kermit)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.koin.core)

            implementation(libs.decompose)
            implementation(libs.essenty.coroutines)

            implementation(libs.result)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "dev.chara.tasks.shared.component"
}

ktfmt {
    kotlinLangStyle()
}