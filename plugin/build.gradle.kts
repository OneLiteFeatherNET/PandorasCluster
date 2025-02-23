plugins {
    id("java")
}

group = "net.onelitefeather"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {

    compileOnly(libs.paper)
    implementation(project(":common"))
    implementation(project(":api"))
    implementation(libs.jetbrainsAnnotations)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}