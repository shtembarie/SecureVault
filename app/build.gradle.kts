plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.bbg.securevault"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bbg.securevault"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.fido)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //Email & Password
    implementation (libs.firebase.auth.ktx)
    // Retrofit and Gson dependencies
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)
    // Coroutines for Flow and suspend functions
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.coil.compose)
    implementation(libs.androidx.material.icons.extended)
    // Maps Implementation
    // Compose Maps
    implementation(libs.maps.compose)
    // Google Maps SDK
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    // Videoplayer
    // Media3 for ExoPlayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    // Compose integration
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.ui)
    // Jetpack Compose
    implementation(libs.androidx.ui.v154)
    implementation(libs.material3)
    // Jetpack Navigation for Compose
    implementation(libs.androidx.navigation.compose)
    //simulates base64 "encryption"
    implementation(libs.androidx.datastore.preferences.v100)
    implementation (libs.accompanist.flowlayout)
    // Import the Firebase BoM
    implementation(libs.firebase.bom)
    // dependency for the Firebase SDK for Google Analytics
    //implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    //Biometrics
    implementation (libs.androidx.biometric)
    // Setting up the SQLCipher
    implementation (libs.android.database.sqlcipher)
    // Optional (if you plan to use Room with SQLCipher)
    implementation(libs.androidx.sqlite)
    implementation(libs.androidx.sqlite.framework)
    implementation(libs.androidx.room.runtime)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.androidx.security.crypto)


}