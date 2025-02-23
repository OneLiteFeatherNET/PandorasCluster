package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.database.mapper.impl.ClaimedChunkMapper;
import net.onelitefeather.pandorascluster.database.mapper.impl.LandAreaMapper;
import net.onelitefeather.pandorascluster.database.mapper.impl.LandMapper;
import net.onelitefeather.pandorascluster.database.mapper.impl.LandPlayerMapper;
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;

public class DatabaseServiceImpl implements DatabaseService, ThreadHelper {

    private SessionFactory sessionFactory;
    private final LandMapper landMapper;
    private final ClaimedChunkMapper chunkMapper;
    private final LandAreaMapper landAreaMapper;
    private final LandPlayerMapper landPlayerMapper;

    public DatabaseServiceImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.chunkMapper = new ClaimedChunkMapper();
        this.landPlayerMapper = new LandPlayerMapper();
        this.landAreaMapper = new LandAreaMapper(this);
        this.landMapper = new LandMapper(this.landAreaMapper, this.landPlayerMapper);
    }

    @Override
    public void connect(@NotNull String configFileResource) {
        syncThreadForServiceLoader(() -> {
            try {
                sessionFactory = new Configuration().configure().configure(configFileResource).buildSessionFactory();
            } catch (HibernateException e) {
                throw new HibernateException("Cannot build session factorty.", e);
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

    @Override
    public @NotNull DatabaseEntityMapper<LandDBO, Land> landMapper() {
        return this.landMapper;
    }

    @Override
    public @NotNull DatabaseEntityMapper<LandAreaDBO, LandArea> landAreaMapper() {
        return this.landAreaMapper;
    }

    @Override
    public @NotNull DatabaseEntityMapper<ClaimedChunkDBO, ClaimedChunk> chunkMapper() {
        return this.chunkMapper;
    }

    @Override
    public @NotNull DatabaseEntityMapper<LandPlayerDBO, LandPlayer> landPlayerMapper() {
        return this.landPlayerMapper;
    }
}
