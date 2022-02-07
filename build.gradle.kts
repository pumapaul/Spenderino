buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
        classpath("com.android.tools.build:gradle:7.1.1")
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.11.0")
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("dev.icerock.moko.kswift") version "0.3.0"
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    apply(from = "${rootDir}/detekt/detekt.gradle")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
