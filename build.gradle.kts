import de.chojo.Repo

plugins {
    kotlin("jvm") version "1.9.22"
    alias(libs.plugins.run.paper)
    alias(libs.plugins.plugin.yml)
    alias(libs.plugins.shadow)
    alias(libs.plugins.liquibase)
    alias(libs.plugins.publishdata)
    `maven-publish`
}

group = "net.onelitefeather"
version = "1.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://libraries.minecraft.net")
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {

    compileOnly(libs.paper)

    //WorldGuard
    compileOnly(libs.worldguard)
    compileOnly(libs.faweCore)
    compileOnly(libs.fawe) {
        isTransitive = false
    }

    // Commands
    implementation(libs.cloudPaper)
    implementation(libs.cloudAnnotations)
    implementation(libs.cloudMinecraftExtras)
    implementation(libs.adventurePlatformBukkit)
    implementation(libs.commodore) {
        isTransitive = false
    }

    implementation(libs.caffeine)

    // Sentry
    implementation(libs.sentry)

    // Database
    implementation(libs.hibernateCore)
    implementation(libs.mariadbJavaClient)
    implementation(libs.hibernateHikariCP)

}

publishData {
    addBuildData()
    addRepo(Repo(Regex(".*"), "SNAPSHOT", "https://gitlab.themeinerlp.dev/api/v4/projects/pandoras-cluster/packages/maven", false, Repo.Type.SNAPSHOT))
    addRepo(Repo(Regex("master"), "", "https://gitlab.themeinerlp.dev/api/v4/projects/pandoras-cluster/packages/maven", false, Repo.Type.STABLE))
    publishTask("shadowJar")
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

    runServer {
        minecraftVersion("1.20.4")
        jvmArgs("-Xmx4G")
    }
}

paper {
    version = publishData.getVersion(true)
    main = "${rootProject.group}.pandorascluster.PandorasClusterPlugin"
    apiVersion = "1.20"
    name = rootProject.name
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    author = "theShadowsDust"
    authors = listOf("OneLiteFeather")

    //Paper
    hasOpenClassloader = false
    generateLibrariesJson = false
    foliaSupported = true

    serverDependencies {
        register("WorldGuard") {
            required = false
        }
    }

    permissions {
        listOf(
            "pandorascluster.command.land.info",
            "pandorascluster.command.land.visit",
            "pandorascluster.command.land.flag.set",
            "pandorascluster.command.land.set.home",
            "pandorascluster.flags.flag.pvp",
            "pandorascluster.flags.flag.pve",
            "pandorascluster.flags.flag.use",
            "pandorascluster.flags.flag.redstone",
            "pandorascluster.flags.flag.potion-splash",
            "pandorascluster.flags.flag.hanging-break",
            "pandorascluster.flags.flag.hanging-place",
            "pandorascluster.flags.flag.vehicle-use",
            "pandorascluster.flags.flag.vehicle-create",
            "pandorascluster.flags.flag.vehicle-damage",
            "pandorascluster.flags.flag.leaves-decay",
            "pandorascluster.flags.flag.entitiy-change-block",
            "pandorascluster.flags.flag.explosions",
            "pandorascluster.flags.flag.mob-griefing",
            "pandorascluster.flags.flag.ice-form",
            "pandorascluster.flags.flag.block-form",
            "pandorascluster.flags.flag.turtle-egg-destroy",
            "pandorascluster.flags.flag.unknown",
            "pandorascluster.flags.flag.interact-crops",
            "pandorascluster.flags.flag.entity-mount",
            "pandorascluster.flags.flag.entity-tame",
            "pandorascluster.flags.flag.bucket-interact",
            "pandorascluster.flags.flag.shear-block",
            "pandorascluster.flags.flag.shear-entity",
            "pandorascluster.flags.flag.take-lectern",
            "pandorascluster.flags.flag.entity-leash",
            "pandorascluster.flags.flag.villager-interact",
            "pandorascluster.flags.flag.fire-protection",
            "pandorascluster.flags.flag.sponge-absorb",
            "pandorascluster.limit.claim",
            "pandorascluster.owned.access",
            "pandorascluster.owned.block.break",
            "pandorascluster.owned.block.place",
            "pandorascluster.owned.entry.denied",
            "pandorascluster.owned.interact.container",

            ).forEach { perm ->
            register(perm) {
                default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.TRUE
            }
        }
        listOf(
            "pandorascluster.unlimit.claim",
            "pandorascluster.command.land.setowner",
            "pandorascluster.admin.set.role",
            "pandorascluster.admin.set.flags",
            "pandorascluster.admin.set.home",
            "pandorascluster.admin.set.owner",
            "pandorascluster.unowned.access",
            "pandorascluster.unowned.block.break",
            "pandorascluster.unowned.block.place",
        ).forEach { perm ->
            register(perm) {
                default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
            }
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        // configure the publication as defined previously.
        publishData.configurePublication(this)
        version = publishData.getVersion(false)
    }

    repositories {
        maven {
            credentials(HttpHeaderCredentials::class) {
                name = "Job-Token"
                value = System.getenv("CI_JOB_TOKEN")
            }
            authentication {
                create("header", HttpHeaderAuthentication::class)
            }


            name = "Gitlab"
            // Get the detected repository from the publish data
            url = uri(publishData.getRepository())
        }
    }
}