plugins {
    kotlin("jvm") version "1.7.10"
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
    maven("https://maven.enginehub.org/repo/")
}

dependencies {

    // Paper
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")

    // Commands
    implementation("cloud.commandframework", "cloud-paper", "1.7.0")
    implementation("cloud.commandframework", "cloud-annotations", "1.7.0")
    implementation("cloud.commandframework", "cloud-minecraft-extras", "1.7.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.1")
    implementation("me.lucko:commodore:2.0") {
        isTransitive = false
    }

    // Sentry
    implementation("org.apache.logging.log4j:log4j-core:2.18.0")
    implementation(libs.sentry)
    implementation(libs.sentryJul)
    implementation(libs.sentrylog4j2)

    // Database
    implementation("org.hibernate:hibernate-core:6.1.1.Final")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.hibernate.orm:hibernate-hikaricp:6.1.1.Final")

    //WorldGuard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly(libs.faweCore)
    compileOnly(libs.fawe) {
        isTransitive = false
    }

    testImplementation("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    testImplementation(libs.faweCore)
    testImplementation(libs.fawe) {
        isTransitive = false

    }

    // Database
    testImplementation("org.hibernate:hibernate-core:6.1.1.Final")
    testImplementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    testImplementation("com.zaxxer:HikariCP:5.0.1")
    testImplementation("org.hibernate.orm:hibernate-hikaricp:6.1.1.Final")

    // Commands
    testImplementation("cloud.commandframework", "cloud-paper", "1.7.0")
    testImplementation("cloud.commandframework", "cloud-annotations", "1.7.0")
    testImplementation("cloud.commandframework", "cloud-minecraft-extras", "1.7.0")
    testImplementation("net.kyori:adventure-platform-bukkit:4.1.1")
    testImplementation("me.lucko:commodore:2.0") {
        isTransitive = false
    }

    // Testing
    testImplementation("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    testImplementation("io.mockk:mockk:1.12.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
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
