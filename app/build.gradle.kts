plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.perf.plugin)
    alias(libs.plugins.firebase.crashlytics.gradle)
}

android {
    namespace = "io.github.jhdcruz.memo"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.jhdcruz.memo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true

            proguardFiles(
                getDefaultProguardFile("proguard-defaults.txt"),
                "proguard-rules-debug.txt",
            )
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-defaults.txt"),
                "proguard-android-optimize.txt",
            )

            packaging {
                resources {
                    excludes += "META-INF/proguard/okhttp3.pro"
                    excludes += "META-INF/proguard/coroutines.pro"
                    excludes += "META-INF/NOTICE.txt"
                    excludes += "META-INF/LICENSE.txt"
                    excludes += "META-INF/DEPENDENCIES.txt"
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }
}

dependencies {

    implementation(platform(libs.compose.bom))
    implementation(platform(libs.firebase.bom))

    implementation(libs.bundles.core)

    implementation(libs.bundles.appcompat)
    implementation(libs.bundles.compose.ui)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.play.services)
    implementation(libs.bundles.firebase)

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.bundles.test.extensions)

    debugImplementation(libs.bundles.compose.debug)

    ksp(libs.hilt.compiler)
}
