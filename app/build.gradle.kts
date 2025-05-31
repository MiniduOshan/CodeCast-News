plugins {
    alias(libs.plugins.android.application)
    // Apply the Google Services plugin to enable Firebase
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.codecastnews"
    compileSdk = 35 // You can keep this or use the latest stable version

    defaultConfig {
        applicationId = "com.example.codecastnews"
        minSdk = 27
        targetSdk = 35 // Should match compileSdk
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
        // Ensure you are using Java 11 or higher for Firebase recent versions
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



    // Firebase BOM (Bill of Materials) - This manages Firebase versions automatically
    // It's crucial to use the latest Firebase BOM version. Check Firebase documentation for updates.
    implementation(platform("com.google.firebase:firebase-bom:32.8.1")) // **Check for the latest stable BOM version!**

    // Firebase Authentication (Email/Password)
    implementation("com.google.firebase:firebase-auth")

    // Google Sign-In (REQUIRED for the Google Login in SignInScreen.java)
    // Make sure to use the latest stable version if 21.0.0 causes issues.
    implementation(libs.play.services.auth)

    // Firebase Realtime Database (as you had this in your original dependencies)
    implementation("com.google.firebase:firebase-database")

    // Glide for image loading
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler) // Essential for Glide to work correctly

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}