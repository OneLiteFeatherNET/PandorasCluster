rootProject.name = "PandorasCluster"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("paper", "io.papermc.paper", "paper-api").version("1.20.4-R0.1-SNAPSHOT")

            //Worldguard
            library("worldguard", "com.sk89q.worldguard", "worldguard-bukkit").version("7.1.0-SNAPSHOT")
            library("fawe", "com.fastasyncworldedit", "FastAsyncWorldEdit-Bukkit").version("2.8.1")
            library("faweCore", "com.fastasyncworldedit", "FastAsyncWorldEdit-Core").version("2.8.1")

            library("cloudPaper", "cloud.commandframework", "cloud-paper").version("1.8.3")
            library("cloudAnnotations", "cloud.commandframework", "cloud-annotations").version("1.8.3")
            library("cloudMinecraftExtras", "cloud.commandframework", "cloud-minecraft-extras").version("1.8.3")
            library("adventurePlatformBukkit", "net.kyori", "adventure-platform-bukkit").version("4.1.2")
            library("commodore", "me.lucko", "commodore").version("2.2")


            library("caffeine", "com.github.ben-manes.caffeine", "caffeine").version("3.1.1")

            // Sentry
            library("apacheLog4j","org.apache.logging.log4j", "log4j-core").version("2.19.0")
            library("sentry", "io.sentry", "sentry").version("6.0.0")
            library("sentryJul", "io.sentry", "sentry-jul").version("6.0.0")
            library("sentrylog4j2", "io.sentry", "sentry-log4j2").version("6.0.0")

            // Database
            library("hibernateCore", "org.hibernate", "hibernate-core").version("6.1.5.Final")
            library("mariadbJavaClient","org.mariadb.jdbc", "mariadb-java-client").version("3.0.6")
            library("hibernateHikariCP","org.hibernate.orm", "hibernate-hikaricp").version("6.1.5.Final")
        }
    }
}