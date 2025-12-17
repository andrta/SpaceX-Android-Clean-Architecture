plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.localstorage"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core:domain"))

    // --- Dependency Injection ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // --- Room ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- Serialization ---
    implementation(libs.kotlinx.serialization.json)

    // --- Simple Preferences ---
    implementation(libs.androidx.datastore)

    // --- Testing ---
    testImplementation(project(":core:testing"))
    testImplementation(libs.androidx.room.testing) // Used to test Room migrations
}
