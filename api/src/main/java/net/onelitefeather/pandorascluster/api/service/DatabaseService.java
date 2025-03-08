package net.onelitefeather.pandorascluster.api.service;

import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

public interface DatabaseService {

    void connect(@NotNull String configFileResource);

    void shutdown();

    boolean isRunning();

    @NotNull
    SessionFactory sessionFactory();
}
