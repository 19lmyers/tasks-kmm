plugins {
    `kotlin-dsl`
    `embedded-kotlin`
}

kotlin {
    // This is hardcoded to force Gradle to target a supported version
    jvmToolchain(17)
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)
    implementation(libs.ktfmt.gradle.plugin)
}