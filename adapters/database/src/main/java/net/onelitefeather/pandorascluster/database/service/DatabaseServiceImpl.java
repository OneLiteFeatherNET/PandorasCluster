package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;

public final class DatabaseServiceImpl implements DatabaseService, ThreadHelper {

    private SessionFactory sessionFactory;

    public DatabaseServiceImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void connect(@NotNull String configFileResource) {
        syncThreadForServiceLoader(() -> {
            try {
                sessionFactory = new Configuration().configure().configure(configFileResource).buildSessionFactory();
            } catch (HibernateException e) {
                throw new HibernateException("Cannot build session factory.", e);
            }
        });
    }

    @Override
    public void shutdown() {
        if(!this.sessionFactory.isOpen()) return;
        this.sessionFactory.close();
    }

    @Override
    public boolean isRunning() {
        return !this.sessionFactory.isClosed();
    }

    @Override
    public @NotNull SessionFactory sessionFactory() {
        return this.sessionFactory;
    }
}
