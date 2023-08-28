import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)

    alias(libs.plugins.jetbrains.compose)

    alias(libs.plugins.ktfmt)
}

kotlin {
    jvmToolchain(17)

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":shared:component"))
                api(project(":shared:ext"))

                api(libs.decompose)
                api(libs.essenty)

                implementation(project(":shared:ui"))

                implementation(libs.crashkios)

                implementation(libs.kermit)
                implementation(libs.kermit.crashlytics)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

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

    targets.withType<KotlinNativeTarget> {
        binaries {
            framework("TasksShared") {
                isStatic = true

                // TODO: remove this once Kotlin 1.9.10 fixes this
                if (System.getenv("XCODE_VERSION_MAJOR") == "1500") {
                    linkerOpts += "-ld_classic"
                    linkerOpts += "-lsqlite3"
                }

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

ktfmt {
    kotlinLangStyle()
}