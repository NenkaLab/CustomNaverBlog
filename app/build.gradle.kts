import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.google.dagger.hilt)
    alias(libs.plugins.google.protobuf)
}

android {
    namespace = "com.nl.customnaverblog"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.nl.customnaverblog"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            applicationIdSuffix = null
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    applicationVariants.configureEach variant@ {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/${this@variant.name}/kotlin")
            }
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            // Enable experimental coroutines APIs, including Flow
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xcontext-receivers",
        )
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.webkit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.compose.material.iconsExtended)

    implementation(libs.androidx.browser)

    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.navigation.testing)

    implementation(libs.androidx.lifecycle.runtimeCompose)
    testImplementation(libs.androidx.lifecycle.runtimeTesting)
    androidTestImplementation(libs.androidx.lifecycle.runtimeTesting)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.viewModelSavedState)
    kapt(libs.androidx.lifecycle.compiler)
    kaptTest(libs.androidx.lifecycle.compiler)
    implementation(libs.androidx.lifecycle.process)

    implementation(libs.android.datastore)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)
    implementation(libs.coil.kt.gif)
    implementation(libs.coil.kt.video)
    implementation(libs.coil.kt.ktor)

    implementation(libs.ktor.core)
    implementation(libs.ktor.android)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.okhttp)

    implementation(libs.avif.coder)

    implementation(libs.timber)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kaptTest(libs.hilt.android.compiler)
    testImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.hilt.android.testing)
    implementation(libs.hilt.navigation.compose) {
        exclude(group = "androidx.navigation", module = "navigation-compose")
    }

    implementation(libs.protobuf)

    implementation(libs.jsoup)

    implementation(libs.paging.compose)
    implementation(libs.paging.runtime.ktx)

    implementation(project(":avif-coder-coil"))
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = libs.protobuf.compiler.get().toString()
    }

    generateProtoTasks {
        all().forEach { tasks ->
            tasks.builtins {
                id("kotlin") {
                    option("lite")
                }
                id("java") {
                    option("lite")
                }
            }
        }
    }
}