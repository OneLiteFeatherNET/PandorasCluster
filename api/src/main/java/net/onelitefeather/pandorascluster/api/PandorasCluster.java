package net.onelitefeather.pandorascluster.api;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.service.*;
import net.onelitefeather.pandorascluster.api.service.result.land.GetLandAreaResult;
import net.onelitefeather.pandorascluster.api.service.result.land.GetLandResult;
import net.onelitefeather.pandorascluster.api.service.result.world.GetLandWorldResult;

import java.util.UUID;

public interface PandorasCluster {

    LandPlayerService getLandPlayerService();

    DatabaseService getDatabaseService();

    LandService getLandService();

    LandFlagService getLandFlagService();

    StaffNotificationService getStaffNotification();

    LandAreaService getLandAreaService();

    GetLandAreaResult getLandArea(long chunkKey);

    GetLandAreaResult getLandArea(ClaimedChunk chunk);

    GetLandResult getLand(UUID playerId, int landId);

    GetLandResult getLand(UUID playerId);

    GetLandResult getLand(LandPlayer landPlayer);

    GetLandWorldResult getLandWorld(UUID worldId);

    FlagRegistry getFlagRegistry();
}
