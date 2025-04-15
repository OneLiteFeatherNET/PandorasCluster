package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandAreaService;
import net.onelitefeather.pandorascluster.api.service.LandService;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.land.LandAreaMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.land.LandMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.position.HomePositionMappingStrategy;
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
    public @NotNull List<Land> getLands() {
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            var query = session.createQuery("SELECT l FROM LandEntity l", LandEntity.class);
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

            MappingContext mappingContext = MappingContext.create();
//            mappingContext.setMappingStrategy(LandMappingStrategy.create());
            mappingContext.setMappingStrategy(LandAreaMappingStrategy.create());
            mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);

            LandEntity landEntity = (LandEntity) mappingContext.doMapping(land);
            session.merge(landEntity);
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

            var flagContainerEntity = new FlagContainerEntity(null, null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
            var landEntity = new LandEntity(null,
                    new LandPlayerEntity(owner.getId(), owner.getUniqueId().toString(), owner.getName()),
                    toHomePositionEntity(home),
                    Collections.emptyList(),
                    flagContainerEntity);

            session.persist(flagContainerEntity.withLand(landEntity));
            session.persist(landEntity);
            session.persist(landEntity.home());

            transaction.commit();
            var land = new Land(landEntity.id(), owner, home, Collections.emptyList(), FlagContainer.EMPTY);

            addLandArea(land, "default", List.of(chunk));

            return land;
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot create land!", e);
            if (transaction != null) transaction.rollback();
        }

        return null;
    }

    @Override
    public void unclaimLand(@NotNull Land land) {

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            removeFlagsFromLand(land);
            land.getAreas().forEach(this.landAreaService::unclaimArea);

            session.remove(land);
            session.remove(toEntity(land.getHome()));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
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
        MappingContext mappingContext = MappingContext.create();
//        mappingContext.setMappingStrategy(LandMappingStrategy.create());
        mappingContext.setMappingStrategy(LandAreaMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.ENTITY_TO_MODEL);
        return (Land) mappingContext.doMapping(land);
    }

    private HomePositionEntity toEntity(HomePosition homePosition) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(HomePositionMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);
        return (HomePositionEntity) mappingContext.doMapping(homePosition);
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
