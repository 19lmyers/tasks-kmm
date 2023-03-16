@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://repo.repsy.io/mvn/chrynan/public")
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "Tasks"

include(":android")
include(":shared")