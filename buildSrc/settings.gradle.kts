dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}