val stripeKey: String by project
val composeVersion: String by project
val koinVersion: String by project

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp") version "1.6.0-1.0.2"
}

android {
    compileSdk = 31
    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true
        // Sets Java compatibility to Java 8
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    defaultConfig {
        applicationId = "de.paulweber.spenderino.android"
        minSdk = 21
        targetSdk = 31
        versionCode = 5
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }

        forEach {
            it.buildConfigField("String", "StripeKey", "\"$stripeKey\"")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-rc01"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildToolsVersion = "30.0.3"
}

kotlin {
    sourceSets {
        debug {
            kotlin.srcDir("build/generated/ksp/debug/kotlin")
        }
        release {
            kotlin.srcDir("build/generated/ksp/release/kotlin")
        }
    }
}

dependencies {
    implementation(project(":shared"))

    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("io.github.raamcosta.compose-destinations:core:1.1.2-beta")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")

    implementation("com.stripe:stripe-android:19.1.0")

    // redeclaration (also in /shared/build.gradle.kts) is necessary for some reason.
    // Without it this build.gradle.kts resolves a different coroutines version because of some dependency
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt") {
        version {
            strictly("1.5.2-native-mt")
        }
    }
    implementation("com.google.accompanist:accompanist-swiperefresh:0.20.3")
    implementation("com.google.accompanist:accompanist-permissions:0.20.3")
    implementation("com.google.accompanist:accompanist-pager:0.20.3")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.20.3")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    ksp("io.github.raamcosta.compose-destinations:ksp:1.1.2-beta")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
}
