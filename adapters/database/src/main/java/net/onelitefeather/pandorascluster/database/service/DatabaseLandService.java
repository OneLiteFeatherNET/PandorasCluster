package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandAreaService;
import net.onelitefeather.pandorascluster.api.service.LandService;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.land.LandMapper;
import net.onelitefeather.pandorascluster.database.mapper.position.HomePositionMapper;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import net.onelitefeather.pandorascluster.database.models.position.HomePositionEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public @Nullable Land getLand(@NotNull Long id) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            LandEntity landEntity = session.find(LandEntity.class, id);
            if (landEntity == null) return null;
            return toModel(landEntity);
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot find land with id %s".formatted(id), e);
            return null;
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
            return lands.stream().map(this::toModel).toList();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot find any land players.", e);
            return Collections.emptyList();
        }
    }

    @Override
    public void updateLandHome(@NotNull HomePosition homePosition, @NotNull UUID ownerId) {
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.merge(toEntity(homePosition));
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
    public void addLandArea(Land land, String name, List<ClaimedChunk> chunks) {

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            LandEntity landEntity = session.byId(LandEntity.class).load(land.getId());
            var landAreaEntity = new LandAreaEntity(null, name, Collections.emptyList(), Collections.emptyList(), landEntity);
            List<ClaimedChunkEntity> claimedChunks = chunks.stream().map(claimedChunk -> new ClaimedChunkEntity(null, claimedChunk.getChunkIndex(), landAreaEntity)).toList();

            session.persist(landAreaEntity);
            claimedChunks.forEach(session::persist);

            transaction.commit();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot add landArea to land", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public @Nullable Land createLand(@NotNull LandPlayer owner, @NotNull HomePosition home, @NotNull ClaimedChunk chunk) {

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Persist in FK-dependency order: home and flag container first (no outgoing
            // non-null FKs), then the land (needs owner, home, flag_container), then the
            // area and its first chunk.
            var homeEntity = toHomePositionEntity(home);
            session.persist(homeEntity);

            var flagContainerEntity = new FlagContainerEntity(null, null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
            session.persist(flagContainerEntity);

            var ownerRef = session.getReference(LandPlayerEntity.class, owner.getId());
            var landEntity = new LandEntity(null, ownerRef, homeEntity, Collections.emptyList(), flagContainerEntity);
            session.persist(landEntity);

            var landAreaEntity = new LandAreaEntity(null, "default", Collections.emptyList(), Collections.emptyList(), landEntity);
            session.persist(landAreaEntity);

            var claimedChunkEntity = new ClaimedChunkEntity(null, chunk.getChunkIndex(), landAreaEntity);
            session.persist(claimedChunkEntity);

            transaction.commit();

            return new Land(landEntity.id(), owner, home, Collections.emptyList(), FlagContainer.EMPTY);
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot create land!", e);
            if (transaction != null) transaction.rollback();
        }

        return null;
    }

    @Override
    public void unclaimLand(@NotNull Land land) {
        removeFlagsFromLand(land);
        land.getAreas().forEach(this.landAreaService::unclaimArea);

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            LandEntity landEntity = session.byId(LandEntity.class).load(land.getId());
            if (landEntity != null) {
                HomePositionEntity homeEntity = (HomePositionEntity) landEntity.home();
                session.remove(landEntity);
                if (homeEntity != null) session.remove(homeEntity);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot unclaim land.", e);
        }
    }

    @Override
    public boolean hasPlayerLand(@NotNull UUID uuid) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            var query = session.createQuery("SELECT l FROM LandEntity l JOIN FETCH l.owner o WHERE o.uuid = :uuid", LandEntity.class);
            query.setParameter("uuid", uuid.toString());
            return !query.list().isEmpty();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Could not find any lands for uuid %s".formatted(uuid), e);
            return false;
        }
    }

    private Land toModel(LandEntity land) {
        return LandMapper.toModel(land);
    }

    private HomePositionEntity toEntity(HomePosition homePosition) {
        return HomePositionMapper.toEntity(homePosition);
    }

    private HomePositionEntity toHomePositionEntity(HomePosition homePosition) {
        return new HomePositionEntity(
                null,
                homePosition.getWorld(),
                homePosition.getPosX(),
                homePosition.getPosY(),
                homePosition.getPosZ(),
                homePosition.getYaw(),
                homePosition.getPitch());
    }

    private void removeFlagsFromLand(Land land) {
        var flagContainer = land.getFlagContainer();
        flagContainer.getEntityCapFlags().forEach(flag -> pandorasCluster.getLandFlagService().removeEntityCapFlag(flag, flagContainer));
        flagContainer.getRoleFlags().forEach(roleFlag -> pandorasCluster.getLandFlagService().removeRoleFlag(roleFlag, flagContainer));
        flagContainer.getNaturalFlags().forEach(naturalFlag -> pandorasCluster.getLandFlagService().removeNaturalFlag(naturalFlag, flagContainer));
    }
}
