// Copyright (c) 2026 shyakdas

import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.hilt.android)
    id("com.google.gms.google-services")
    jacoco
}

android {
    namespace = "com.moneytrack"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.moneytrack"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH")
            if (keystorePath != null) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        abortOnError = true
        warningsAsErrors = true
        disable +=
            setOf(
                "AndroidGradlePluginVersion",
                "GradleDependency",
                "NewerVersionAvailable",
            )
    }

    flavorDimensions += "environment"

    productFlavors {

        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "ENVIRONMENT", "\"DEV\"")
        }

        create("prod") {
            dimension = "environment"
            buildConfigField("String", "ENVIRONMENT", "\"PROD\"")
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.generateKotlin", "true")
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDevDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter =
        listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "**/Hilt_*.*",
            "**/*_Factory*.*",
            "**/*_Provide*Factory*.*",
            "**/*_MembersInjector*.*",
            "**/*_GeneratedInjector*.*",
            "**/*HiltModules*.*",
            "**/hilt_aggregated_deps/**",
            "**/dagger/hilt/**",
        )

    val coverageIncludes =
        listOf(
            "com/moneytrack/feature/**",
            "com/moneytrack/navigation/AppDestination*",
            "com/moneytrack/startup/AppEntryUiState*",
            "com/moneytrack/startup/AppEntryViewModel*",
            "com/moneytrack/onboarding/domain/**",
            "com/moneytrack/onboarding/data/repository/**",
            "com/moneytrack/onboarding/presentation/OnboardingAction*",
            "com/moneytrack/onboarding/presentation/OnboardingViewModel*",
        )

    val debugTree =
        fileTree(
            layout.buildDirectory.dir(
                "intermediates/javac/devDebug",
            ),
        ) {
            include(coverageIncludes)
            exclude(fileFilter)
        }

    val kotlinDebugTree =
        fileTree(
            layout.buildDirectory.dir(
                "intermediates/built_in_kotlinc/devDebug/compileDevDebugKotlin/classes",
            ),
        ) {
            include(coverageIncludes)
            exclude(fileFilter)
        }

    classDirectories.setFrom(
        files(
            debugTree,
            kotlinDebugTree,
        ),
    )

    sourceDirectories.setFrom(
        files(
            "src/main/java",
            "src/main/kotlin",
        ),
    )

    executionData.setFrom(
        fileTree(
            layout.buildDirectory,
        ) {
            include(
                "jacoco/testDevDebugUnitTest.exec",
                "outputs/unit_test_code_coverage/devDebugUnitTest/testDevDebugUnitTest.exec",
            )
        },
    )
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}

dependencies {
    implementation(project(":DesignSystem"))

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.lottie.compose)

    // Unit Tests
    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.paparazzi)

    // UI Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Splash
    implementation(libs.androidx.core.splashscreen)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)

    // Hilt DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
}
