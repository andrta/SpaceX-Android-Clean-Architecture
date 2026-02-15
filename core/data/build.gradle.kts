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

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.apollo.runtime)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.hilt.core)
    ksp(libs.hilt.compiler)

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(project(":core:testing"))
}

apollo {
    service("service") {
        packageName.set("com.example.data")

        introspection {
            endpointUrl.set("https://spacex-production.up.railway.app/")
            schemaFile.set(file("src/main/graphql/com/spacex/core/data/schema.graphqls"))
        }
    }
}
