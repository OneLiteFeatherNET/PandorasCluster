package net.onelitefeather.pandorascluster.api;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.land.LandWorld;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.service.*;
import net.onelitefeather.pandorascluster.api.service.result.GetLandAreaResult;
import net.onelitefeather.pandorascluster.api.service.result.GetLandResult;
import net.onelitefeather.pandorascluster.api.service.result.GetLandWorldResult;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.service.*;
import net.onelitefeather.pandorascluster.database.service.flag.DatabaseLandFlagService;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

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
        this.landFlagService = new DatabaseLandFlagService(this);
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
    public Optional<LandArea> getLandArea(long chunkKey) {

        var landArea = switch (this.landAreaService.getLandArea(chunkKey)) {
            case GetLandAreaResult.Found(LandArea found) -> found;
            case GetLandAreaResult.Failed(String msg, Throwable throwable) -> {
                Constants.LOGGER.log(Level.SEVERE, msg, throwable);
                yield null;
            }
            case GetLandAreaResult.NotFound ignored -> null;
        };

        return Optional.ofNullable(landArea);
    }

    @Override
    public Optional<LandArea> getLandArea(ClaimedChunk chunk) {
        return this.getLandArea(chunk.chunkIndex());
    }

    @Override
    public Optional<Land> getLand(UUID playerId, int landId) {

        var land = switch (this.landService.getLand(playerId, landId)) {
            case GetLandResult.Found(Land found) -> found;
            case GetLandResult.Failed(String msg, Throwable throwable) -> {
                Constants.LOGGER.log(Level.SEVERE, msg, throwable);
                yield null;
            }
            case GetLandResult.NotFound ignored -> null;
        };

        return Optional.ofNullable(land);
    }

    @Override
    public Optional<Land> getLand(UUID playerId) {
        return this.getLand(playerId, 1);
    }

    @Override
    public Optional<Land> getLand(LandPlayer landPlayer) {
        return this.getLand(landPlayer.uniqueId());
    }

    @Override
    public Optional<LandWorld> getLandWorld(UUID worldId) {

        var landWorld = switch (this.landService.getLandWorld(worldId)) {
            case GetLandWorldResult.Found(LandWorld world) -> world;
            case GetLandWorldResult.Failed(String msg, Throwable throwable) -> {
                Constants.LOGGER.log(Level.SEVERE, msg, throwable);
                yield null;
            }
            case GetLandWorldResult.NotFound ignored -> null;
        };

        return Optional.ofNullable(landWorld);
    }

    @Override
    public FlagRegistry getFlagRegistry() {
        return this.flagRegistry;
    }
}
