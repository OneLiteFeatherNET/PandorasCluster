package net.onelitefeather.pandorascluster.service

import net.onelitefeather.pandorascluster.land.ChunkPlaceholder
import net.onelitefeather.pandorascluster.land.Land
import net.onelitefeather.pandorascluster.land.flag.LandFlagEntity
import net.onelitefeather.pandorascluster.land.player.LandMember
import net.onelitefeather.pandorascluster.land.player.LandPlayer
import net.onelitefeather.pandorascluster.land.position.HomePosition
import org.hibernate.SessionFactory
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment
import org.hibernate.dialect.MariaDBDialect
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider
import org.hibernate.tool.schema.Action
import java.util.*

class DatabaseService(var jdbcUrl: String,
                      var username: String,
                      var password: String,
                      var driver: String) {

    var sessionFactory: SessionFactory

    init {
        val configuration = Configuration()
        val properties = Properties()
        properties[Environment.URL] = jdbcUrl
        properties[Environment.DRIVER] = driver
        properties[Environment.USER] = username
        properties[Environment.PASS] = password
        properties[Environment.IMPLICIT_NAMING_STRATEGY] = ImplicitNamingStrategyLegacyJpaImpl::class.java
        properties[Environment.CONNECTION_PROVIDER] = HikariCPConnectionProvider::class.java
        properties[Environment.DIALECT] = MariaDBDialect()
        properties[Environment.HBM2DDL_AUTO] = Action.UPDATE
        //        properties.put(Environment.SHOW_SQL, true);

        configuration.properties = properties
        configuration.addAnnotatedClass(Land::class.java)
        configuration.addAnnotatedClass(LandPlayer::class.java)
        configuration.addAnnotatedClass(LandMember::class.java)
        configuration.addAnnotatedClass(LandFlagEntity::class.java)
        configuration.addAnnotatedClass(HomePosition::class.java)
        configuration.addAnnotatedClass(ChunkPlaceholder::class.java)
        val registry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
        sessionFactory = configuration.buildSessionFactory(registry)
    }

    fun shutdown() {
        sessionFactory.close()
    }
}