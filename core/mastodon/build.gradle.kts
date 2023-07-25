plugins {
    kotlin("plugin.serialization") version Versions.KOTLIN
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = Versions.java
    targetCompatibility = Versions.java
}

repositories {
    maven {
        url = uri("https://repo.repsy.io/mvn/chrynan/public")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(Dependencies.KTOR_CIO)
    implementation(Dependencies.KTOR_CONTENT_NEGOTIATION)
    implementation(Dependencies.KTOR_CORE)
    implementation(Dependencies.KTOR_SERIALIZATION_KOTLINX_JSON)
    implementation(Dependencies.PAGINATE)
}