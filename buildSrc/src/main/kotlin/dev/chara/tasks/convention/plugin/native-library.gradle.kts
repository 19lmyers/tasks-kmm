package dev.chara.tasks.convention.plugin

import dev.chara.tasks.convention.Versions

plugins {
    id("org.jetbrains.kotlin.multiplatform")

    id("com.ncorti.ktfmt.gradle")
}

kotlin {
    jvmToolchain(Versions.JVM_TOOLCHAIN)
}

ktfmt {
    kotlinLangStyle()
}