// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android Application (for module :app)
    alias(libs.plugins.androidApplication) apply false

    // Android Library (for modules :core e :features)
    alias(libs.plugins.androidLibrary) apply false

    // Kotlin Android
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false

    // Kotlin Serialization (for parsing JSON REST)
    alias(libs.plugins.jetbrainsKotlinSerialization) apply false

    // KSP (Kotlin Symbol Processing) - Essential for Room and Hilt
    alias(libs.plugins.ksp) apply false

    // Hilt (Dependency Injection)
    alias(libs.plugins.hilt) apply false

    // Apollo GraphQL (needed to generate models from the files .graphql)
    alias(libs.plugins.apollo) apply false
}
