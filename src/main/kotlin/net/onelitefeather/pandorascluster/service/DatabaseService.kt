package net.onelitefeather.pandorascluster.service

import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.land.ChunkPlaceholder
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.player.LandMember
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.HomePosition
import org.bukkit.Bukkit
import org.hibernate.SessionFactory
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment
import org.hibernate.dialect.MariaDBDialect
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider
import org.hibernate.tool.schema.Action
import java.util.*

class DatabaseService(
    pandorasClusterPlugin: PandorasClusterPlugin,
    jdbcUrl: String,
    username: String,
    password: String,
    driver: String
) {

    lateinit var sessionFactory: SessionFactory

    init {
        val configuration = Configuration()
        val properties = Properties()
        properties[Environment.JAKARTA_JDBC_URL] = jdbcUrl
        properties[Environment.JAKARTA_JDBC_DRIVER] = driver
        properties[Environment.JAKARTA_JDBC_USER] = username
        properties[Environment.JAKARTA_JDBC_PASSWORD] = password
        properties[Environment.IMPLICIT_NAMING_STRATEGY] = ImplicitNamingStrategyLegacyJpaImpl::class.java
        properties[Environment.CONNECTION_PROVIDER] = HikariCPConnectionProvider::class.java
        properties[Environment.DIALECT] = MariaDBDialect()
        properties[Environment.HBM2DDL_AUTO] = Action.UPDATE.name.lowercase()

        configuration.properties = properties
        configuration.addAnnotatedClass(Land::class.java)
        configuration.addAnnotatedClass(LandPlayer::class.java)
        configuration.addAnnotatedClass(LandMember::class.java)
        configuration.addAnnotatedClass(LandFlagEntity::class.java)
        configuration.addAnnotatedClass(HomePosition::class.java)
        configuration.addAnnotatedClass(ChunkPlaceholder::class.java)
        val registry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
        try {
            sessionFactory = configuration.buildSessionFactory(registry)
        } catch (e: Exception) {
            Bukkit.getPluginManager().disablePlugin(pandorasClusterPlugin)
        }
    }

    fun shutdown() {
        sessionFactory.close()
    }

    fun isRunning() =  this::sessionFactory.isInitialized && sessionFactory.isOpen
}
