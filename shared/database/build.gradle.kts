plugins {
    id("dev.chara.tasks.convention.plugin.android-library")

    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kotlin.atomicfu)

    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:model"))

            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.sqlite.android)
            implementation(libs.sqldelight.android.driver)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "dev.chara.tasks.shared.database"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

sqldelight {
    databases {
        create("SQLDatabase") {
            dialect(libs.sqldelight.dialect)

            packageName.set("dev.chara.tasks.shared.database.sql")
            version = 1

            deriveSchemaFromMigrations.set(true)
            verifyMigrations.set(true)
        }
    }
}