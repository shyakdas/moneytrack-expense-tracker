import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Zero style debates, consistent diffs, fast PR reviews
    alias(libs.plugins.ktlint) apply false

    // God classes, long methods, bad Kotlin patterns
    alias(libs.plugins.detekt) apply false

    // Screenshot testing
    alias(libs.plugins.paparazzi) apply false

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.4" apply false
}

subprojects {

    plugins.withId("com.android.application") {
        extensions.configure<ApplicationExtension> {
            compileSdk = 36

            defaultConfig {
                minSdk = 24
            }

            buildFeatures {
                compose = true
            }
        }
    }

    plugins.withId("com.android.library") {
        extensions.configure<LibraryExtension> {
            compileSdk = 36

            defaultConfig {
                minSdk = 24
            }

            buildFeatures {
                compose = true
            }
        }
    }
}
