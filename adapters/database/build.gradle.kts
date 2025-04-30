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
    implementation("org.postgresql:postgresql:42.7.5") //DATABASE

    implementation("net.kyori:adventure-api:4.21.0")

    compileOnly(project(":api"))

}