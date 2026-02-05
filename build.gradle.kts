// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Zero style debates, Consistent diffs, Fast PR reviews
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1" apply false
    // God classes, Long methods, Bad Kotlin patterns
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
}
