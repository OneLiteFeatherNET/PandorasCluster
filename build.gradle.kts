plugins {
    id("java")
//    checkstyle
    // FIXME
    // Bukkit
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("xyz.jpenilla.run-paper") version "1.0.6"

    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.liquibase.gradle") version "2.1.0"
    id("org.sonarqube") version "3.4.0.2513"
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
    implementation("me.lucko:commodore:2.0") {
        isTransitive = false
    }
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0");

    // Database
    implementation("org.hibernate:hibernate-core:6.1.1.Final")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.5")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.hibernate.orm:hibernate-hikaricp:6.1.0.Final")

    implementation("me.lucko:helper:5.6.10")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {

    compileJava {
        options.release.set(17)
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("1.19")
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}.${archiveExtension.getOrElse("jar")}")
    }
}

bukkit {
    main = "${rootProject.group}.pandorascluster.PandorasClusterPlugin"
    apiVersion = "1.18"
    name = "PandorasCluster"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    authors = listOf("UniqueGame", "OneLiteFeather")

    depend = listOf("ProtocolLib")
}
sonarqube {
    properties {
        property("sonar.projectKey", "onelitefeather_projects_pandoras-cluster_AYImhlbRTSfGYIFfefLS")
    }
}
