rootProject.name = "PandorasCluster"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://eldonexus.de/repository/maven-public/")
    }
}

dependencyResolutionManagement {
    if (System.getenv("CI") != null) {
        repositoriesMode = RepositoriesMode.PREFER_SETTINGS
        repositories {
            maven("https://repo.htl-md.schule/repository/Gitlab-Runner/")
        }
    }
    versionCatalogs {
        create("libs") {

            version("paper", "1.20.6-R0.1-SNAPSHOT")
            version("plugin.yml", "0.6.0")
            version("run-paper", "2.3.1")
            version("publishdata", "1.2.5-DEV")
            version("shadow", "8.1.1")
            version("liquibase", "2.2.2")

            plugin("plugin.yml", "net.minecrell.plugin-yml.paper").versionRef("plugin.yml")
            plugin("run.paper", "xyz.jpenilla.run-paper").versionRef("run-paper")
            plugin("publishdata", "de.chojo.publishdata").versionRef("publishdata")
            plugin("shadow", "com.github.johnrengelman.shadow").versionRef("shadow")
            plugin("liquibase", "org.liquibase.gradle").versionRef("liquibase")

            library("paper", "io.papermc.paper", "paper-api").versionRef("paper")

            //Worldguard
            library("worldguard", "com.sk89q.worldguard", "worldguard-bukkit").version("7.1.0-SNAPSHOT")
            library("fawe", "com.fastasyncworldedit", "FastAsyncWorldEdit-Bukkit").version("2.9.0")
            library("faweCore", "com.fastasyncworldedit", "FastAsyncWorldEdit-Core").version("2.11.1")

            library("cloudPaper", "cloud.commandframework", "cloud-paper").version("1.8.4")
            library("cloudAnnotations", "cloud.commandframework", "cloud-annotations").version("1.8.3")
            library("cloudMinecraftExtras", "cloud.commandframework", "cloud-minecraft-extras").version("1.8.4")
            library("adventurePlatformBukkit", "net.kyori", "adventure-platform-bukkit").version("4.3.4")
            library("commodore", "me.lucko", "commodore").version("2.2")


            library("caffeine", "com.github.ben-manes.caffeine", "caffeine").version("3.1.1")

            // Database
            library("hibernateCore", "org.hibernate", "hibernate-core").version("6.5.2.Final")
            library("mariadbJavaClient","org.mariadb.jdbc", "mariadb-java-client").version("3.4.1")
            library("hibernateHikariCP","org.hibernate.orm", "hibernate-hikaricp").version("6.5.2.Final")
        }
    }
}