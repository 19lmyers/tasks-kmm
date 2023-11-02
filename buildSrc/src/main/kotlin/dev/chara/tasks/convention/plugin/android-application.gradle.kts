package dev.chara.tasks.convention.plugin

import dev.chara.tasks.convention.Versions

plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.application")

    id("com.ncorti.ktfmt.gradle")
}

kotlin {
    jvmToolchain(Versions.jvmToolchain)
}

android {
    compileSdk = Versions.compileSdk
    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
    }
}

ktfmt {
    kotlinLangStyle()
}