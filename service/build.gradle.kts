plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.serialization)
}

group = "ru.alex3koval.ehub"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.pulsar)
    implementation(libs.bundles.kotlinx.serialization)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.bundles.pulsar)
    implementation(libs.exposed.core)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}