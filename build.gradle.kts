// Copyright (c) 2026 shyakdas

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

    // Dependency injection
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false

    // Screenshot testing
    alias(libs.plugins.paparazzi) apply false

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.4" apply false
}

buildscript {
    dependencies {
        constraints {
            // Security overrides for transitive build-time dependencies
            classpath("org.bouncycastle:bcpkix-jdk18on:1.80")
            classpath("org.bouncycastle:bcprov-jdk18on:1.80")
            classpath("org.bouncycastle:bcutil-jdk18on:1.80")
            classpath("org.bitbucket.b_c:jose4j:0.9.6")
            classpath("org.jdom:jdom2:2.0.6.1")
            classpath("org.apache.commons:commons-compress:1.28.0")
            classpath("org.apache.commons:commons-lang3:3.17.0")
            classpath("ch.qos.logback:logback-core:1.5.18")

            // Netty family advisories
            classpath("io.netty:netty-codec:4.1.118.Final")
            classpath("io.netty:netty-codec-http:4.1.118.Final")
            classpath("io.netty:netty-codec-http2:4.1.118.Final")
            classpath("io.netty:netty-handler:4.1.118.Final")
        }
    }
}

subprojects {

    plugins.withId("com.android.application") {
        extensions.configure<ApplicationExtension> {
            compileSdk = 37

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
            compileSdk = 37

            defaultConfig {
                minSdk = 24
            }

            buildFeatures {
                compose = true
            }
        }
    }
}
