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
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://repo.repsy.io/mvn/chrynan/public")
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "Tasks"

include(":shared:model")
include(":shared:database")
include(":shared:data")
include(":shared:domain")
include(":shared:component")
include(":shared:ui")
include(":shared:ext")
include(":shared:framework")

include(":android")