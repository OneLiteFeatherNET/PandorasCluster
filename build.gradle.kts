plugins {
    kotlin("jvm") version "1.7.10"
//    checkstyle
    // FIXME
    // Bukkit
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("xyz.jpenilla.run-paper") version "1.0.6"

    // LIQUIBASE
    // alias(libs.plugins.liquibase)
    // SonarQube
    // id("org.sonarqube") version "3.4.0.2513"
}

group = "net.onelitefeather"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://libraries.minecraft.net")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://jitpack.io")
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    // Commands
    implementation("cloud.commandframework", "cloud-paper", "1.7.0")
    implementation("cloud.commandframework", "cloud-annotations", "1.7.0")
    implementation("cloud.commandframework", "cloud-minecraft-extras", "1.7.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.1")
    implementation("me.lucko:commodore:2.0") {
        isTransitive = false
    }
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0");

    // Database
    implementation("org.hibernate:hibernate-core:6.1.1.Final")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.hibernate.orm:hibernate-hikaricp:6.1.1.Final")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {

    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}*/
/*sonarqube {
    properties {
        property("sonar.projectKey", "cliar_alwilda-loup_AYHtte8H7chqtZHGSV5T")
        property("sonar.qualitygate.wait", true)
    }
}
*/
