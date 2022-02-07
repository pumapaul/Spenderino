val ktorVersion: String by project
val koinVersion: String by project
val logbackVersion: String by project
val kermitVersion: String by project
val coroutineVersion: String by project

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("dev.icerock.moko.kswift")
    kotlin("plugin.serialization") version "1.6.10"
    id("com.codingfeline.buildkonfig")
}

version = "1.0"

@Suppress("UnusedPrivateMember")
kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
        // iosSimulatorArm64() sure all ios dependencies support this target
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
            isStatic = false
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion") {
                    version {
                        strictly(coroutineVersion)
                    }
                }
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("co.touchlab:kermit:$kermitVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.insert-koin:koin-test:$koinVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
                implementation("io.mockk:mockk:1.12.2")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.insert-koin:koin-android:$koinVersion")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        // val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            // iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        // val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            // iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}

// Generate swift enums from kotlin sealed classes
kswift {
    install(dev.icerock.moko.kswift.plugin.feature.SealedToSwiftEnumFeature)
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink>().matching {
    it.binary is org.jetbrains.kotlin.gradle.plugin.mpp.Framework
}.configureEach {
    doLast {
        val swiftDirectory = File(destinationDir, "${binary.baseName}Swift")
        val xcodeSwiftDirectory = File(rootDir, "ios/Spenderino/Generated")
        swiftDirectory.copyRecursively(xcodeSwiftDirectory, overwrite = true)
    }
}

buildkonfig {
    packageName = "de.paulweber.spenderino.utility"

    val stripeKey: String by project
    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "StripeKey",
            stripeKey
        )
    }
}
// CodeCoverage:
tasks.koverHtmlReport {
    excludes = listOf(
        "de.paulweber.spenderino.test.*",
        "*\$\$serializer",
        "*\$\$inlined\$inject\$default*"
    )
}

tasks.koverVerify {
    excludes = listOf(
        "de.paulweber.spenderino.test.*",
        "*\$\$serializer",
        "*\$\$inlined\$inject\$default*"
    )

    rule {
        name = "Minimal line coverage rate in percent"
        bound {
            minValue = 75
        }
    }
}
