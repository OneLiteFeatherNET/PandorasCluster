plugins {
    id("java")
}

dependencies {

    implementation(libs.caffeine)

    // Database
    implementation(libs.hibernateCore)
    implementation(libs.mariadbJavaClient)
    implementation(libs.hibernateHikariCP)

    implementation(project(":adapters:database"))
    implementation(project(":api"))

    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation("org.postgresql:postgresql:42.7.7") //DATABASE

    testImplementation(project(":adapters:database"))
    testImplementation(project(":api"))

    testImplementation(platform("org.junit:junit-bom:5.13.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.test {
    useJUnitPlatform()
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}