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
    implementation(libs.h2) // For testing purposes

    // Minecraft Component API
    implementation(libs.adventureApi)

    compileOnly(project(":api"))

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}