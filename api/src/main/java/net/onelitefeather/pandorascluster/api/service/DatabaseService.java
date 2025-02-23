package net.onelitefeather.pandorascluster.api.service;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.DatabaseEntityMapper;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.dbo.chunk.ClaimedChunkDBO;
import net.onelitefeather.pandorascluster.dbo.flag.FlagContainerDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandAreaDBO;
import net.onelitefeather.pandorascluster.dbo.land.LandDBO;
import net.onelitefeather.pandorascluster.dbo.player.LandPlayerDBO;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;

public interface DatabaseService {

    void connect(@NotNull String configFileResource);

    void shutdown();

    boolean isRunning();

    @NotNull
    SessionFactory sessionFactory();

    @NotNull
    DatabaseEntityMapper<LandDBO, Land> landMapper();

    @NotNull
    DatabaseEntityMapper<LandAreaDBO, LandArea> landAreaMapper();

    @NotNull
    DatabaseEntityMapper<ClaimedChunkDBO, ClaimedChunk> chunkMapper();

    @NotNull
    DatabaseEntityMapper<LandPlayerDBO, LandPlayer> landPlayerMapper();

    @NotNull
    DatabaseEntityMapper<FlagContainerDBO, FlagContainer> flagContainerMapper();

}
