package net.onelitefeather.pandorascluster.api;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.land.LandWorld;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.service.*;

import java.util.Optional;
import java.util.UUID;

public interface PandorasCluster {

    LandPlayerService getLandPlayerService();

    DatabaseService getDatabaseService();

    LandService getLandService();

    LandFlagService getLandFlagService();

    StaffNotificationService getStaffNotification();

    LandAreaService getLandAreaService();

    Optional<LandArea> getLandArea(long chunkKey);

    Optional<LandArea> getLandArea(ClaimedChunk chunk);

    Optional<Land> getLand(UUID playerId, int landId);

    Optional<Land> getLand(UUID playerId);

    Optional<Land> getLand(LandPlayer landPlayer);

    Optional<LandWorld> getLandWorld(UUID worldId);

    FlagRegistry getFlagRegistry();
}
