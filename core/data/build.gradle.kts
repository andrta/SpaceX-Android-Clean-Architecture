plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.apollo)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core:domain"))

    // --- Networking ---
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // --- GraphQL ---
    implementation(libs.apollo.runtime)

    // --- Serialization ---
    implementation(libs.kotlinx.serialization.json)

    // --- Dependency Injection ---
    implementation(libs.hilt.core)
    ksp(libs.hilt.compiler)

    // --- Coroutines ---
    implementation(libs.kotlinx.coroutines.core)

    // --- Testing ---
    testImplementation(project(":core:testing"))
}

apollo {
    service("service") {
        packageName.set("com.example.data")

        // Configuration to automatically download the schemas
        introspection {
            endpointUrl.set("https://spacex-production.up.railway.app/")
            // Location to store the downloaded file
            schemaFile.set(file("src/main/graphql/com/spacex/core/data/schema.graphqls"))
        }
    }
}
