@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kotlin.atomicfu)

    alias(libs.plugins.android.library)

    alias(libs.plugins.moko.kswift)

    alias(libs.plugins.sqldelight)

    alias(libs.plugins.kmmbridge)

    id("kotlin-parcelize")

    `maven-publish`
}

kotlin {
    jvmToolchain(8)

    android()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    /*
    @OptIn(org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl::class)
    wasm {
        browser()
        binaries.executable()
    }
    */

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.napier)

                implementation(libs.kotlinx.datetime)

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines.core)

                api(libs.koin.core)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation(libs.moko.kswift.runtime)

                api(libs.moko.mvvm.core)
                api(libs.moko.mvvm.flow)

                implementation(libs.sqldelight.coroutines.extensions)

                implementation(libs.datastore)

                implementation(libs.okio)

                api(libs.result)
                implementation(libs.result.coroutines)

                implementation(libs.uuid)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.lifecycle.viewmodel)

                implementation(libs.ktor.client.okhttp)

                implementation(libs.sqlite.android)
                implementation(libs.sqldelight.android.driver)
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
                implementation(libs.ktor.client.darwin)
                implementation(libs.sqldelight.native.driver)

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

    targets.withType<KotlinNativeTarget> {
        binaries {
            framework("TasksShared")
        }
    }
}

// For whatever reason, these must be here and not in androidMain
dependencies {
    implementation(platform(libs.firebase.bom))
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

android {
    namespace = "dev.chara.tasks"
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

kmmbridge {
    mavenPublishArtifacts()
    githubReleaseVersions()
    spm()
    versionPrefix.set("0.8")
    addGithubPackagesRepository()
}

sqldelight {
    databases {
        create("CacheDatabase") {
            dialect(libs.sqldelight.dialect)

            packageName.set("dev.chara.tasks.data.cache.sql")
            version = 1

            deriveSchemaFromMigrations.set(true)
            verifyMigrations.set(true)
        }
    }
}