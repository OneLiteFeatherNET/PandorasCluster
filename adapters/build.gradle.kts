plugins {
    id("java")
}

dependencies {
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