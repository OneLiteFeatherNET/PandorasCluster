package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.flag.FlagRegistry;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandWorld;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandAreaService;
import net.onelitefeather.pandorascluster.api.service.LandService;
import net.onelitefeather.pandorascluster.api.service.result.land.CreateLandResult;
import net.onelitefeather.pandorascluster.api.service.result.land.GetLandResult;
import net.onelitefeather.pandorascluster.api.service.result.world.CreateLandWorldResult;
import net.onelitefeather.pandorascluster.api.service.result.world.DeleteLandWorldResult;
import net.onelitefeather.pandorascluster.api.service.result.world.GetLandWorldResult;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.flag.NaturalFlagMapper;
import net.onelitefeather.pandorascluster.database.mapper.land.LandMapper;
import net.onelitefeather.pandorascluster.database.mapper.land.LandWorldMapper;
import net.onelitefeather.pandorascluster.database.mapper.position.HomePositionMapper;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.flag.WorldNaturalFlagEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandWorldEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class DatabaseLandService implements LandService {

    private final PandorasCluster pandorasCluster;
    private final LandAreaService landAreaService;
    private final DatabaseService databaseService;

    public DatabaseLandService(PandorasCluster cluster) {
        this.pandorasCluster = cluster;
        this.landAreaService = cluster.getLandAreaService();
        this.databaseService = cluster.getDatabaseService();
    }

    @Override
    public @NotNull GetLandResult getLand(@NotNull Long id) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            var query = session.createQuery(
                    "SELECT l FROM LandEntity l " +
                            "LEFT JOIN l.owner lo ON l.owner = lo " +
                            "LEFT JOIN FETCH l.home " +
                            "LEFT JOIN FETCH l.flagContainerEntity " +
                            "LEFT JOIN FETCH l.areas WHERE lo.uuid = :uuid AND l.id = :id",
                    LandEntity.class);

            query.setParameter("id", id);
            var result = query.uniqueResult();

            if (result == null) return new GetLandResult.NotFound();
            return new GetLandResult.Found(LandMapper.toModel(result));
        } catch (HibernateException e) {
            var result = new GetLandResult.Failed("Cannot find land with id %s".formatted(id), e);
            Constants.LOGGER.log(Level.SEVERE, result.message(), e);
            return result;
        }
    }

    @Override
    public @NotNull GetLandResult getLand(@NotNull UUID ownerId, long landId) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            var query = session.createQuery(
                    "SELECT l FROM LandEntity l " +
                            "LEFT JOIN l.owner lo ON l.owner = lo " +
                            "LEFT JOIN FETCH l.home " +
                            "LEFT JOIN FETCH l.flagContainerEntity " +
                            "LEFT JOIN FETCH l.areas WHERE lo.uuid = :uuid AND l.id = :id",
                    LandEntity.class);

            query.setParameter("uuid", ownerId.toString());
            query.setParameter("id", landId);
            var result = query.uniqueResult();

            if (result == null) return new GetLandResult.NotFound();
            return new GetLandResult.Found(LandMapper.toModel(result));
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot find land with ownerId %s and id %s".formatted(ownerId.toString(), landId), e);
            return new GetLandResult.Failed("Cannot find land with ownerId %s and id %s".formatted(ownerId.toString(), landId), e);
        }
    }

    @Override
    public @NotNull List<Land> getLands() {
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            var query = session.createQuery(
                    "SELECT DISTINCT l FROM LandEntity l " +
                            "LEFT JOIN FETCH l.owner " +
                            "LEFT JOIN FETCH l.home " +
                            "LEFT JOIN FETCH l.flagContainerEntity " +
                            "LEFT JOIN FETCH l.areas",
                    LandEntity.class);
            var lands = query.list();
            return lands.stream().map(LandMapper::toModel).toList();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot find any land players.", e);
            return Collections.emptyList();
        }
    }

    @Override
    public @NotNull List<Land> getLands(LandWorld world) {
        return List.of();
    }

    @Override
    public void updateLandHome(@NotNull HomePosition homePosition, @NotNull UUID ownerId) {
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.merge(HomePositionMapper.toEntity(homePosition));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot update land home with x: %s y: %s z: %s".formatted(
                    homePosition.getBlockX(),
                    homePosition.getBlockY(),
                    homePosition.getBlockZ()), e);
        }
    }

    @Override
    public void updateLand(@NotNull Land land) {
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(LandMapper.toEntity(land));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot update land!", e);
        }
    }

    @Override
    public @NotNull CreateLandResult createLand(@NotNull LandPlayer owner, @NotNull LandWorld landWorld, @NotNull HomePosition home, @NotNull ClaimedChunk chunk) {

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Persist in FK-dependency order: home and flag container first (no outgoing
            // non-null FKs), then the land (needs owner, home, flag_container), then the
            // area and its first chunk.
            var homeEntity = HomePositionMapper.toEntity(home);
            session.persist(homeEntity);

            var flagContainerEntity = new FlagContainerEntity(null, null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
            session.persist(flagContainerEntity);

            var ownerRef = session.getReference(LandPlayerEntity.class, owner.id());
            var landEntity = new LandEntity(null, ownerRef, homeEntity, Collections.emptyList(), LandWorldMapper.toEntity(landWorld), flagContainerEntity);
            session.persist(landEntity);

            var landAreaEntity = new LandAreaEntity(null, "default", Collections.emptyList(), Collections.emptyList(), landEntity);
            session.persist(landAreaEntity);

            var claimedChunkEntity = new ClaimedChunkEntity(null, chunk.chunkIndex(), landAreaEntity);
            session.persist(claimedChunkEntity);

            transaction.commit();

            return new CreateLandResult.Created(new Land(landEntity.id(), owner, home, Collections.emptyList(), FlagContainer.EMPTY, landWorld));
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot create land!", e);
            if (transaction != null) transaction.rollback();
            return new CreateLandResult.Failed("Cannot create land", e);
        }
    }

    @Override
    public void unclaimLand(@NotNull Land land) {
        removeFlagsFromLand(land);
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            var landEntity = LandMapper.toEntity(land);
            session.remove(landEntity.home());
            session.remove(landEntity.areas());
            session.remove(landEntity);

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot unclaim land.", e);
        }
    }

    @Override
    public CreateLandWorldResult createLandWorld(UUID worldId, String name) {

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();

            var landWorldEntity = new LandWorldEntity(null, worldId.toString(), name, Collections.emptyList(), Collections.emptyList());
            session.persist(landWorldEntity);

            // Flush to ensure the entity is persisted and has an ID
            session.flush();

            // Create flags that reference the persisted entity
            var flags = FlagRegistry.getNaturalFlags()
                    .stream()
                    .map(naturalFlag -> new WorldNaturalFlagEntity(null, naturalFlag.getName(), naturalFlag.getDefaultState(), landWorldEntity))
                    .toList();

            flags.forEach(session::persist);
            transaction.commit();

            var world = new LandWorld(landWorldEntity.id(), worldId, name, Collections.emptyList(), Collections.emptyList());
            return new CreateLandWorldResult.Created(world);
        } catch (HibernateException e) {
            var message = "Failed to create land world %s!".formatted(worldId.toString());
            Constants.LOGGER.log(Level.SEVERE, message, e);
            if (transaction != null) transaction.rollback();
            return new CreateLandWorldResult.Failed(message, e);
        }
    }

    @Override
    public DeleteLandWorldResult deleteLandWorld(UUID worldId) {

        LandWorld landWorld = getLandWorld(worldId) instanceof GetLandWorldResult.Found(LandWorld world) ? world : null;

        if (landWorld == null) return new DeleteLandWorldResult.NotFound();

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();

            landWorld.naturalFlags().stream().map(NaturalFlagMapper::toEntity).forEach(session::remove);
            landWorld.lands().stream().map(LandMapper::toEntity).forEach(session::remove);
            session.remove(LandWorldMapper.toEntity(landWorld));
            transaction.commit();

            return new DeleteLandWorldResult.Success(landWorld);
        } catch (HibernateException e) {
            var result = new DeleteLandWorldResult.Failed("Failed to delete land world %s!".formatted(worldId.toString()), e);
            ;
            Constants.LOGGER.log(Level.SEVERE, result.message(), e);
            if (transaction != null) transaction.rollback();
            return result;
        }
    }

    @Override
    public GetLandWorldResult getLandWorld(UUID worldId) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            var query = session.createQuery("SELECT lw FROM LandWorldEntity lw " +
                    "LEFT JOIN FETCH lw.naturalFlags WHERE lw.uuid = :uuid", LandWorldEntity.class);

            query.setParameter("uuid", worldId.toString());
            var landWorld = query.uniqueResult();
            if (landWorld == null) return new GetLandWorldResult.NotFound();

            var lands = new ArrayList<>(landWorld.lands().stream().map(LandMapper::toModel).toList());
            var found = LandWorldMapper.toModel(landWorld);
            // Create a new LandWorld with the lands included
            var foundWithLands = new LandWorld(found.id(), found.worldId(), found.name(), found.naturalFlags(), lands);
            return new GetLandWorldResult.Found(foundWithLands);
        } catch (HibernateException e) {
            String message = "Cannot find land with worldId %s".formatted(worldId.toString());
            Constants.LOGGER.log(Level.SEVERE, message, e);
            return new GetLandWorldResult.Failed(message, e);
        }
    }

    @Override
    public GetLandWorldResult getLandWorld(long id) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            String queryStr = "SELECT lw FROM LandWorldEntity lw LEFT JOIN FETCH lw.naturalFlags WHERE lw.id = :id";
            var query = session.createQuery(queryStr, LandWorldEntity.class);

            query.setParameter("id", id);
            var result = query.uniqueResult();

            if (result == null) return new GetLandWorldResult.NotFound();
            return new GetLandWorldResult.Found(LandWorldMapper.toModel(result));
        } catch (HibernateException e) {
            String message = "Cannot find land with id %s".formatted(id);
            Constants.LOGGER.log(Level.SEVERE, message, e);
            return new GetLandWorldResult.Failed(message, e);
        }
    }

    private void removeFlagsFromLand(Land land) {
        var flagContainer = land.flagContainer();
        flagContainer.entityCapFlags().forEach(flag -> pandorasCluster.getLandFlagService().removeEntityCapFlag(flag, flagContainer));
        flagContainer.roleFlags().forEach(roleFlag -> pandorasCluster.getLandFlagService().removeRoleFlag(roleFlag, flagContainer));
        flagContainer.naturalFlags().forEach(naturalFlag -> pandorasCluster.getLandFlagService().removeNaturalFlag(naturalFlag, flagContainer));
    }
}
