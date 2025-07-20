import org.gradle.kotlin.dsl.mavenCentral

rootProject.name = "pandoras-cluster"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://libraries.minecraft.net")
        maven("https://jitpack.io")
        maven("https://maven.enginehub.org/repo/")
    }
    versionCatalogs {
        create("libs") {

            version("paper", "1.21.4-R0.1-SNAPSHOT")
            version("plugin.yml", "0.6.0")
            version("run-paper", "2.3.1")
            version("shadow", "8.1.1")
            version("liquibase", "2.2.2")
            version("guava", "33.3.1-jre")

            plugin("plugin.yml", "net.minecrell.plugin-yml.paper").versionRef("plugin.yml")
            plugin("run.paper", "xyz.jpenilla.run-paper").versionRef("run-paper")
            plugin("shadow", "com.github.johnrengelman.shadow").versionRef("shadow")
            plugin("liquibase", "org.liquibase.gradle").versionRef("liquibase")

            library("paper", "io.papermc.paper", "paper-api").versionRef("paper")

            //Worldguard
            library("worldguard", "com.sk89q.worldguard", "worldguard-bukkit").version("7.1.0-SNAPSHOT")
            library("fawe", "com.fastasyncworldedit", "FastAsyncWorldEdit-Bukkit").version("2.11.1")
            library("faweCore", "com.fastasyncworldedit", "FastAsyncWorldEdit-Core").version("2.13.0")
            library("jetbrainsAnnotations", "org.jetbrains", "annotations").version("26.0.2")

            // Commands
            library("cloudPaper", "org.incendo", "cloud-paper").version("2.0.0-beta.10")
            library("cloudAnnotations", "org.incendo", "cloud-annotations").version("2.0.0")
            library("cloudMinecraftExtras", "org.incendo", "cloud-minecraft-extras").version("2.0.0-SNAPSHOT")
            library("adventurePlatformBukkit", "net.kyori", "adventure-platform-bukkit").version("4.3.4")

            library("caffeine", "com.github.ben-manes.caffeine", "caffeine").version("3.2.0")

            // Database
            library("hibernateCore", "org.hibernate", "hibernate-core").version("6.6.9.Final")
            library("mariadbJavaClient","org.mariadb.jdbc", "mariadb-java-client").version("3.5.2")
            library("hibernateHikariCP","org.hibernate.orm", "hibernate-hikaricp").version("6.6.22.Final")
        }
    }
}
include("api")
include("adapters:bukkit")
include("adapters:database")
include("plugin")
include("common")
