import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.perf.plugin)
    alias(libs.plugins.firebase.crashlytics.gradle)
    alias(libs.plugins.ktlint.gradle)
}

android {
    namespace = "io.github.jhdcruz.memo"
    compileSdk = 34

    val properties = Properties()
    properties.load(rootProject.file("local.properties").reader())

    defaultConfig {
        applicationId = "io.github.jhdcruz.memo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            "String",
            "GCP_WEB_CLIENT",
            "\"${properties.getProperty("gcp.web.client")}\"",
        )
        buildConfigField(
            "String",
            "GCP_WEB_SECRET",
            "\"${properties.getProperty("gcp.web.secret")}\"",
        )
    }

    if (!providers.environmentVariable("CI").isPresent) {
        signingConfigs {
            create("release") {
                storeFile = file(properties.getProperty("signing.release.store.file"))
                storePassword = properties.getProperty("signing.release.store.password")
                keyAlias = properties.getProperty("signing.release.key.alias")
                keyPassword = properties.getProperty("signing.release.key.password")
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "proguard-rules-debug.txt",
            )

            buildConfigField(
                "String",
                "GCP_CLIENT",
                "\"${properties.getProperty("gcp.client.debug")}\"",
            )
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            if (!providers.environmentVariable("CI").isPresent) {
                signingConfig = signingConfigs.getByName("release")
            }

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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

            buildConfigField(
                "String",
                "GCP_CLIENT",
                "\"${properties.getProperty("gcp.client.release")}\"",
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(platform(libs.compose.bom))
    implementation(platform(libs.firebase.bom))

    implementation(libs.bundles.core)

    implementation(libs.bundles.appcompat)
    implementation(libs.bundles.compose.ui)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.play.services)
    implementation(libs.bundles.android.credentials)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.external)

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.bundles.test.extensions)

    debugImplementation(libs.bundles.compose.debug)

    ksp(libs.hilt.compiler)

    ktlintRuleset(libs.ktlint.compose.rules)
}
