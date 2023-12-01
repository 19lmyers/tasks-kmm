package dev.chara.tasks.convention.plugin

import dev.chara.tasks.convention.Versions

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")

    id("com.ncorti.ktfmt.gradle")
}

kotlin {
    jvmToolchain(Versions.JVM_TOOLCHAIN)
}

android {
    compileSdk = Versions.COMPILE_SDK
    defaultConfig {
        minSdk = Versions.MIN_SDK
    }
}

ktfmt {
    kotlinLangStyle()
}