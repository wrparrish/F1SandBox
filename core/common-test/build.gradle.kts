plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.parrishdev.common.test"
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
    // Core common module for DispatcherProvider, ViewModelBundle, etc.
    implementation(project(":core:common"))

    // Lifecycle for TestLifecycleOwner
    api(libs.androidx.lifecycle.runtime.ktx)

    // Test utilities exposed as API so consumers get them transitively
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.mockk)
}
