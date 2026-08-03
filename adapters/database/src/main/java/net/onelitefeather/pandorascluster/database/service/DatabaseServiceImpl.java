package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

public record DatabaseServiceImpl(SessionFactory sessionFactory) implements DatabaseService, ThreadHelper {

    @Override
    public void shutdown() {
        if (!this.sessionFactory.isOpen()) return;
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
