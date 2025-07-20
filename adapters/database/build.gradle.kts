plugins {
    id("java")
}

dependencies {

    // Database
    implementation(libs.hibernateCore)
    implementation(libs.mariadbJavaClient)
    implementation(libs.hibernateHikariCP)

    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation("org.postgresql:postgresql:42.7.4") //DATABASE

    implementation("net.kyori:adventure-api:4.23.0")

    compileOnly(project(":api"))

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}