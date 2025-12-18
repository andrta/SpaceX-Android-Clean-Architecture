plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.ksp)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // --- Coroutines ---
    implementation(libs.kotlinx.coroutines.core)

    // --- Hilt ---
    implementation(libs.hilt.core)
    ksp(libs.hilt.compiler)
}
