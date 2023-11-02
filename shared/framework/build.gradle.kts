import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("dev.chara.tasks.convention.plugin.native-library")

    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:component"))
            api(project(":shared:ext"))

            api(libs.decompose)
            api(libs.essenty)

            implementation(project(":shared:ui"))

            implementation(libs.crashkios)

            implementation(libs.kermit)
            implementation(libs.kermit.crashlytics)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries {
            framework("TasksShared") {
                isStatic = true

                export(project(":shared:component"))
                export(project(":shared:ext"))

                export(libs.decompose)
                export(libs.essenty)
            }
        }
    }
}

compose {
    kotlinCompilerPlugin.set(dependencies.compiler.auto)
}