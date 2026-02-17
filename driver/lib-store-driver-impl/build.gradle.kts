plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.parrishdev.driver.store.impl"
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
    implementation(project(":driver:lib-store-driver"))

    // Domain models
    implementation(project(":driver:lib-models-driver"))

    // API models
    implementation(project(":driver:lib-models-driver-api"))

    // API
    implementation(project(":driver:lib-api-driver"))

    // Database
    implementation(project(":driver:lib-db-driver"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
