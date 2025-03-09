plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.guava)

    // Database
    implementation(libs.hibernateCore)
    implementation(libs.mariadbJavaClient)
    implementation(libs.hibernateHikariCP)

    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation("org.postgresql:postgresql:42.7.4") //DATABASE
    implementation("com.google.code.gson:gson:2.12.1")

    implementation("net.kyori:adventure-api:4.19.0")
    testImplementation("com.h2database:h2:2.3.232")
}

tasks {
    test {
        useJUnitPlatform()
    }
}