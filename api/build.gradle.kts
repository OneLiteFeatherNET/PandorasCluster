plugins {
    kotlin("jvm") version "2.0.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.caffeine)

    // Database
    implementation(libs.hibernateCore)
    implementation(libs.mariadbJavaClient)
    implementation(libs.hibernateHikariCP)

    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation("org.postgresql:postgresql:42.7.4") //DATABASE

    implementation("net.kyori:adventure-api:4.17.0")
    testImplementation("com.h2database:h2:2.3.232")
    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}
