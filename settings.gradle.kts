pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()

        maven("https://jitpack.io")
    }
}

rootProject.name = "Spenderino"
include(":android")
include(":shared")
