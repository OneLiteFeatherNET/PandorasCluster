plugins {
    kotlin("jvm") version "1.7.10"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("xyz.jpenilla.run-paper") version "1.0.6"

    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.liquibase.gradle") version "2.1.0"
    id("org.sonarqube") version "3.4.0.2513"
    jacoco
}

group = "net.onelitefeather"
val baseVersion = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://libraries.minecraft.net")
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {

    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")

    //WorldGuard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly(libs.faweCore)
    compileOnly(libs.fawe) {
        isTransitive = false
    }

    // Commands
    bukkitLibrary("cloud.commandframework", "cloud-paper", "1.7.1")
    bukkitLibrary("cloud.commandframework", "cloud-annotations", "1.7.1")
    bukkitLibrary("cloud.commandframework", "cloud-minecraft-extras", "1.7.1")
    bukkitLibrary("net.kyori:adventure-platform-bukkit:4.1.2")
    bukkitLibrary("me.lucko:commodore:2.2") {
        isTransitive = false
    }

    bukkitLibrary("com.github.ben-manes.caffeine:caffeine:3.1.1")

    // Sentry
    bukkitLibrary("org.apache.logging.log4j:log4j-core:2.19.0")
    bukkitLibrary(libs.sentry)
    bukkitLibrary(libs.sentryJul)
    bukkitLibrary(libs.sentrylog4j2)

    // Database
    implementation("org.hibernate:hibernate-core:6.1.4.Final")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    implementation("org.hibernate.orm:hibernate-hikaricp:6.1.3.Final")

    testImplementation("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    testImplementation(libs.faweCore)
    testImplementation(libs.fawe) {
        isTransitive = false
    }

    // Database
    testImplementation("org.hibernate:hibernate-core:6.1.3.Final")
    testImplementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    testImplementation("com.zaxxer:HikariCP:5.0.1")
    testImplementation("org.hibernate.orm:hibernate-hikaricp:6.1.3.Final")

    // Commands
    testImplementation("cloud.commandframework", "cloud-paper", "1.7.0")
    testImplementation("cloud.commandframework", "cloud-annotations", "1.7.0")
    testImplementation("cloud.commandframework", "cloud-minecraft-extras", "1.7.0")
    testImplementation("net.kyori:adventure-platform-bukkit:4.1.2")
    testImplementation("me.lucko:commodore:2.2") {
        isTransitive = false
    }

    // Testing
    testImplementation("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    testImplementation("io.mockk:mockk:1.12.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    test {
        finalizedBy(rootProject.tasks.jacocoTestReport)
        useJUnitPlatform()
    }

    jacocoTestReport {
        dependsOn(rootProject.tasks.test)
        reports {
            xml.required.set(true)
        }
    }
    getByName<org.sonarqube.gradle.SonarQubeTask>("sonarqube") {
        dependsOn(rootProject.tasks.test)
    }

    runServer {
        minecraftVersion("1.19.2")
        jvmArgs("-Xmx4G")
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}.${archiveExtension.getOrElse("jar")}")
    }
}

bukkit {
    main = "${rootProject.group}.pandorascluster.PandorasClusterPlugin"
    apiVersion = "1.19"
    name = "PandorasCluster"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    authors = listOf("UniqueGame", "OneLiteFeather")
    softDepend = listOf("WorldGuard")
}
sonarqube {
    properties {
        property("sonar.projectKey", "cliar_alwilda-loup_AYHtte8H7chqtZHGSV5T")
        property("sonar.qualitygate.wait", true)
    }
}
version = if (System.getenv().containsKey("CI")) {
    "${baseVersion}+${System.getenv("CI_COMMIT_SHORT_SHA")}"
} else {
    baseVersion
}
