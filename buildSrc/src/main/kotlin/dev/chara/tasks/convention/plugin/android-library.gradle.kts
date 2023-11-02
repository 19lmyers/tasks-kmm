package dev.chara.tasks.convention.plugin

import dev.chara.tasks.convention.Versions

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")

    id("com.ncorti.ktfmt.gradle")
}

kotlin {
    jvmToolchain(Versions.jvmToolchain)
}

android {
    compileSdk = Versions.compileSdk
    defaultConfig {
        minSdk = Versions.minSdk
    }
}

ktfmt {
    kotlinLangStyle()
}