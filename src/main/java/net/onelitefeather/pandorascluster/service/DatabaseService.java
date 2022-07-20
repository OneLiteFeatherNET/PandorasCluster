package net.onelitefeather.pandorascluster.service;

import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.chunk.WorldChunk;
import net.onelitefeather.pandorascluster.chunk.flag.ChunkFlag;
import net.onelitefeather.pandorascluster.land.position.HomePosition;
import net.onelitefeather.pandorascluster.player.ChunkPlayer;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.hibernate.tool.schema.Action;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public final class DatabaseService {

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String driver;
    private SessionFactory sessionFactory;

    public DatabaseService(@NotNull PandorasClusterPlugin plugin, @NotNull String jdbcUrl, @NotNull String username, @NotNull String password, @NotNull String driver) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.driver = driver;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getDriver() {
        return driver;
    }

    public void init() {

        var configuration = new Configuration();
        var properties = new Properties();
        properties.put(Environment.URL, this.getJdbcUrl());
        properties.put(Environment.DRIVER, this.getDriver());
        properties.put(Environment.USER, this.getUsername());
        properties.put(Environment.PASS, this.getPassword());
        properties.put(Environment.IMPLICIT_NAMING_STRATEGY, ImplicitNamingStrategyLegacyJpaImpl.class);

        properties.put(Environment.CONNECTION_PROVIDER, HikariCPConnectionProvider.class);
        properties.put(Environment.DIALECT, new MariaDBDialect());
        properties.put(Environment.HBM2DDL_AUTO, Action.UPDATE);

        configuration.setProperties(properties);
        configuration.addAnnotatedClass(ChunkPlayer.class);
        configuration.addAnnotatedClass(HomePosition.class);
        configuration.addAnnotatedClass(WorldChunk.class);
        configuration.addAnnotatedClass(ChunkFlag.class);

        var registry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        this.sessionFactory = configuration.buildSessionFactory(registry);
    }

    public void shutdown() {
        if (this.sessionFactory != null) {
            this.sessionFactory.close();
        }
    }
}
