plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.parrishdev.race.store.impl"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    //DispatcherProvider
    implementation(project(":core:common"))

    // Store interface
    implementation(project(":race:lib-store-race"))

    // Domain models
    implementation(project(":race:lib-models-race"))

    // API models
    implementation(project(":race:lib-models-race-api"))

    // API
    implementation(project(":race:lib-api-race"))

    // Database
    implementation(project(":race:lib-db-race"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
