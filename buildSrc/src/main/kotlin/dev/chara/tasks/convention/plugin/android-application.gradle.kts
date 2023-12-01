package dev.chara.tasks.convention.plugin

import dev.chara.tasks.convention.Versions

plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.application")

    id("com.ncorti.ktfmt.gradle")
}

kotlin {
    jvmToolchain(Versions.JVM_TOOLCHAIN)
}

android {
    compileSdk = Versions.COMPILE_SDK
    defaultConfig {
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
    }
}

ktfmt {
    kotlinLangStyle()
}