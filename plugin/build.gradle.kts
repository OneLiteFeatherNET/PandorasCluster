plugins {
    id("java")
    alias(libs.plugins.run.paper)
    alias(libs.plugins.plugin.yml)
    alias(libs.plugins.shadow)
    alias(libs.plugins.liquibase)
    `maven-publish`
}

dependencies {

    compileOnly(libs.paper)

    //WorldGuard
//    compileOnly(libs.worldguard)
    compileOnly(libs.faweCore)
    compileOnly(libs.fawe) {
        isTransitive = false
    }

    // Commands
    implementation(libs.cloudPaper)
    implementation(libs.cloudAnnotations)
    implementation(libs.cloudMinecraftExtras)
    implementation(libs.adventurePlatformBukkit)

    // Database
    implementation(libs.hibernateCore)
    implementation(libs.hibernateHikariCP)

    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation("org.postgresql:postgresql:42.7.4") //DATABASE

    implementation(project(":api"))
    implementation(project(":common"))

}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.4")
        jvmArgs("-Xmx4G", "-Dcom.mojang.eula.agree=true")
    }
}

paper {
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
        register("WorldEdit") {
            required = true
        }

        register("WorldGuard") {
            required = false
        }
    }

    permissions {
        listOf(
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
            "pandorascluster.flags.flag.sponge-absorb"
        ).forEach { perm ->
            register(perm) {
                default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.FALSE
            }
        }

        listOf(
            "pandorascluster.command.land.info",
            "pandorascluster.command.land.visit",
            "pandorascluster.command.land.flag.set",
            "pandorascluster.command.land.set.home",
            "pandorascluster.limit.claim"
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
            "pandorascluster.owned.entry.denied",
            "pandorascluster.unowned.access",
            "pandorascluster.unowned.block.break",
            "pandorascluster.unowned.block.place",
            "pandorascluster.owned.access",
            "pandorascluster.owned.block.break",
            "pandorascluster.owned.block.place",
            "pandorascluster.owned.interact.container"
        ).forEach { perm ->
            register(perm) {
                default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
            }
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifact(project.tasks.getByName("shadowJar"))
        version = rootProject.version as String
        artifactId = "pandoras-cluster"
        groupId = rootProject.group as String
        pom {
            name = "PandorasCluster"
            description =
                "A simple land management plugin for OneLiteFeather servers, providing basic features and utilities."
            url = "https://github.com/OneLiteFeatherNET/PandorasCluster"
            licenses {
                license {
                    name = "AGPL-3.0"
                    url = "https://www.gnu.org/licenses/agpl-3.0.en.html"
                }
            }
            developers {
                developer {
                    id = "theShadowsDust"
                    name = "theShadowsDust"
                    email = "theShadowDust@onelitefeather.net"
                }
                developer {
                    id = "themeinerlp"
                    name = "Phillipp Glanz"
                    email = "p.glanz@madfix.me"
                }
            }
            scm {
                connection = "scm:git:git://github.com:OneLiteFeatherNET/PandorasCluster.git"
                developerConnection = "scm:git:ssh://git@github.com:OneLiteFeatherNET/PandorasCluster.git"
                url = "https://github.com/OneLiteFeatherNET/PandorasCluster"
            }
        }
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    // Those credentials need to be set under "Settings -> Secrets -> Actions" in your repository
                    username = System.getenv("ONELITEFEATHER_MAVEN_USERNAME")
                    password = System.getenv("ONELITEFEATHER_MAVEN_PASSWORD")
                }
            }

            name = "OneLiteFeatherRepository"
            val releasesRepoUrl = uri("https://repo.onelitefeather.dev/onelitefeather-releases")
            val snapshotsRepoUrl = uri("https://repo.onelitefeather.dev/onelitefeather-snapshots")
            url =
                if (version.toString().contains("SNAPSHOT") || version.toString().contains("BETA") || version.toString()
                        .contains("ALPHA")
                ) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}