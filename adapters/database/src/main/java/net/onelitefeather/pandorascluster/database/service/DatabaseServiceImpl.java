package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

public final class DatabaseServiceImpl implements DatabaseService, ThreadHelper {

    private final SessionFactory sessionFactory;

    public DatabaseServiceImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
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
