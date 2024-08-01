package net.onelitefeather.pandorascluster.service

import net.onelitefeather.pandorascluster.PandorasClusterPlugin
import net.onelitefeather.pandorascluster.util.ThreadHelper
import org.bukkit.Bukkit
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

class DatabaseService(
    pandorasClusterPlugin: PandorasClusterPlugin
) : ThreadHelper {

    lateinit var sessionFactory: SessionFactory

    init {
        syncThreadForServiceLoader {
            try {
                sessionFactory = Configuration().configure().configure(pandorasClusterPlugin.dataFolder.toPath().resolve("hibernate.cfg.xml").toFile()).buildSessionFactory()
            } catch (e: Exception) {
                Bukkit.getPluginManager().disablePlugin(pandorasClusterPlugin)
            }
        }

    }

    fun shutdown() {
        sessionFactory.close()
    }

    fun isRunning() =  this::sessionFactory.isInitialized && sessionFactory.isOpen
}