package net.onelitefeather.pandorascluster.api;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.service.*;
import net.onelitefeather.pandorascluster.api.service.result.land.GetLandAreaResult;
import net.onelitefeather.pandorascluster.api.service.result.land.GetLandResult;
import net.onelitefeather.pandorascluster.api.service.result.world.GetLandWorldResult;
import net.onelitefeather.pandorascluster.database.service.*;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

import java.util.UUID;

public class PandorasClusterImpl implements PandorasCluster, ThreadHelper {

    private DatabaseService databaseService;
    private final LandPlayerService landPlayerService;
    private final LandFlagService landFlagService;
    private final LandService landService;
    private final LandAreaService landAreaService;
    private final StaffNotificationService staffNotificationService;
    private final FlagRegistry flagRegistry;

    public PandorasClusterImpl() {

        syncThreadForServiceLoader(() -> {
            try {
                var sessionFactory = new Configuration().configure().configure("connection.cfg.xml").buildSessionFactory();
                this.databaseService = new DatabaseServiceImpl(sessionFactory);
            } catch (HibernateException e) {
                throw new IllegalStateException("Failed to build Hibernate SessionFactory — plugin cannot start", e);
            }
        });

        this.landPlayerService = new DatabaseLandPlayerService(databaseService);
        this.landFlagService = new DatabaseLandFlagService(databaseService);
        this.landAreaService = new DatabaseLandAreaService(databaseService);
        this.landService = new DatabaseLandService(this);
        this.staffNotificationService = new StaffNotificationService();
        this.flagRegistry = new FlagRegistry();
        this.flagRegistry.loadDefaultFlags();
    }

    @Override
    public LandPlayerService getLandPlayerService() {
        return this.landPlayerService;
    }

    @Override
    public DatabaseService getDatabaseService() {
        return this.databaseService;
    }

    @Override
    public LandService getLandService() {
        return this.landService;
    }

    @Override
    public LandFlagService getLandFlagService() {
        return this.landFlagService;
    }

    @Override
    public StaffNotificationService getStaffNotification() {
        return this.staffNotificationService;
    }

    @Override
    public LandAreaService getLandAreaService() {
        return this.landAreaService;
    }

    @Override
    public GetLandAreaResult getLandArea(long chunkKey) {
        return this.landAreaService.getLandArea(chunkKey);
    }

    @Override
    public GetLandAreaResult getLandArea(ClaimedChunk chunk) {
        return this.getLandArea(chunk.chunkIndex());
    }

    @Override
    public GetLandResult getLand(UUID playerId, int landId) {
        return this.landService.getLand(playerId, landId);
    }

    @Override
    public GetLandResult getLand(UUID playerId) {
        return this.getLand(playerId, 1);
    }

    @Override
    public GetLandResult getLand(LandPlayer landPlayer) {
        return this.getLand(landPlayer.uniqueId());
    }

    @Override
    public GetLandWorldResult getLandWorld(UUID worldId) {
        return this.landService.getLandWorld(worldId);
    }

    @Override
    public FlagRegistry getFlagRegistry() {
        return this.flagRegistry;
    }
}
