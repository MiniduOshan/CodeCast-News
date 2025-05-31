plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services) // Apply the Google Services plugin
}

android {
    namespace = "com.example.codecastnews"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.codecastnews"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    // AndroidX Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase BOM (Bill of Materials) - ALWAYS check for the latest stable version
    // Find the latest version here: https://firebase.google.com/docs/android/setup#available-libraries
    implementation(platform("com.google.firebase:firebase-bom:32.8.1")) // Check this for updates!

    // Firebase Authentication (Email/Password)
    implementation("com.google.firebase:firebase-auth")

    // Google Sign-In (if used for authentication)
    implementation(libs.play.services.auth)

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")

    // Glide for image loading
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler) // ESSENTIAL for Glide to work correctly

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}