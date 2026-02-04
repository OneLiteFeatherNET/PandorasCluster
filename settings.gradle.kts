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

            // Non Paper
            version("liquibase", "2.2.2")
            version("guava", "33.3.1-jre")
            version("jaxb-runtime", "4.0.6")
            version("caffeine", "3.2.3")
            version("h2", "2.4.240")
            version("mariadb-java-client", "3.5.7")
            version("hibernate-core", "7.2.3.Final")
            version("jetbrains-annotations", "26.0.2-1")
            version("postgresql", "42.7.8")
            version("gson", "2.13.2")

             // Testing
            version("junit", "6.0.1")

            // Paper Dependencies
            version("paper", "1.21.8-R0.1-SNAPSHOT")
            version("adventure-api", "4.25.0")
            version("cloud", "2.0.0-SNAPSHOT")
            version("cloudAnnotations", "2.0.0")
            version("adventure-platform-bukkit", "4.4.1")
            version("fawe", "2.14.3")
            version("worldguard", "7.1.0-SNAPSHOT")

            // Gradle Plugins
            version("plugin.yml", "0.6.0")
            version("run-paper", "3.0.2")
            version("shadow", "8.1.1")

            // Paper
            library("paper", "io.papermc.paper", "paper-api").versionRef("paper")

            //Worldguard
            library("worldguard", "com.sk89q.worldguard", "worldguard-bukkit").versionRef("worldguard")
            library("fawe", "com.fastasyncworldedit", "FastAsyncWorldEdit-Bukkit").versionRef("fawe")
            library("faweCore", "com.fastasyncworldedit", "FastAsyncWorldEdit-Core").versionRef("fawe")
            library("jetbrainsAnnotations", "org.jetbrains", "annotations").versionRef("jetbrains-annotations")

            // Commands
            library("cloudPaper", "org.incendo", "cloud-paper").versionRef("cloud")
            library("cloudAnnotations", "org.incendo", "cloud-annotations").versionRef("cloudAnnotations")
            library("cloudMinecraftExtras", "org.incendo", "cloud-minecraft-extras").versionRef("cloud")
            library("adventurePlatformBukkit", "net.kyori", "adventure-platform-bukkit").versionRef("adventure-platform-bukkit")
            library("adventureApi", "net.kyori", "adventure-api").versionRef("adventure-api")

            // Caching
            library("caffeine", "com.github.ben-manes.caffeine", "caffeine").versionRef("caffeine")

            // Database
            library("hibernateCore", "org.hibernate", "hibernate-core").versionRef("hibernate-core")
            library("mariadbJavaClient","org.mariadb.jdbc", "mariadb-java-client").versionRef("mariadb-java-client")
            library("hibernateHikariCP","org.hibernate.orm", "hibernate-hikaricp").versionRef("hibernate-core")
            library("postgresql", "org.postgresql", "postgresql").versionRef("postgresql")
            library("h2", "com.h2database", "h2").versionRef("h2")

            // XML
            library("jaxbRuntime", "org.glassfish.jaxb", "jaxb-runtime").versionRef("jaxb-runtime")

            // JSON
            library("gson", "com.google.code.gson", "gson").versionRef("gson")

            // Testing
            library("junitBom", "org.junit", "junit-bom").versionRef("junit")
            library("junitApi", "org.junit.jupiter", "junit-jupiter-api").withoutVersion()

            // Plugins
            plugin("plugin.yml", "net.minecrell.plugin-yml.paper").versionRef("plugin.yml")
            plugin("run.paper", "xyz.jpenilla.run-paper").versionRef("run-paper")
            plugin("shadow", "com.github.johnrengelman.shadow").versionRef("shadow")
            plugin("liquibase", "org.liquibase.gradle").versionRef("liquibase")
        }
    }
}
include("api")
include("adapters:bukkit")
include("adapters:database")
include("plugin")
include("common")
