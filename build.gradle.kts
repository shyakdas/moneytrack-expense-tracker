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
}
