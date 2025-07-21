plugins {
    id("java")
}
dependencies {

    // Database
    implementation(libs.hibernateCore)
    implementation(libs.mariadbJavaClient)
    implementation(libs.hibernateHikariCP)
    implementation(libs.jaxbRuntime) // JAXB Runtime for XML binding of hibernate
    implementation(libs.postgresql)
    implementation(libs.h2)
    // JSON
    implementation(libs.gson)

    // Minecraft Component API
    implementation(libs.adventureApi)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}