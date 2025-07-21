plugins {
    id("java")
}

dependencies {

    // Caching
    implementation(libs.caffeine)

    // Database
    implementation(libs.hibernateCore)
    implementation(libs.mariadbJavaClient)
    implementation(libs.hibernateHikariCP)

    implementation(project(":adapters:database"))
    implementation(project(":api"))

    implementation(libs.jaxbRuntime) // JAXB Runtime for XML binding of hibernate
    implementation(libs.postgresql) // For PostgreSQL database

    testImplementation(project(":adapters:database"))
    testImplementation(project(":api"))

    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitApi)

}

tasks.test {
    useJUnitPlatform()
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}