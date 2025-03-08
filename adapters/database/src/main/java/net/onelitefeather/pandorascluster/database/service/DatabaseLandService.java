package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.flag.FlagContainer;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandAreaService;
import net.onelitefeather.pandorascluster.api.service.LandService;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.flag.FlagContainerMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.land.LandAreaMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.land.LandMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.position.HomePositionMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.flag.FlagContainerEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
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

public class DatabaseLandService implements LandService {

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
            mappingContext.setMappingStrategy(LandMappingStrategy.create());
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
        LandArea landArea = new LandArea(
                null,
                name,
                chunks,
                Collections.emptyList(),
                land);

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(LandAreaMappingStrategy.create());
            mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);

            LandAreaEntity landAreaEntity = (LandAreaEntity) mappingContext.doMapping(landArea);
            session.persist(landAreaEntity);
            landArea.getChunks().stream().map(mappingContext::doMapping).forEach(session::persist);

            transaction.commit();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot add landArea to land", e);
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public @Nullable Land createLand(@NotNull LandPlayer owner, @NotNull HomePosition home, @NotNull ClaimedChunk chunk, @NotNull String world) {

        var landArea = this.landAreaService.getLandArea(chunk);
        if (landArea != null) return landArea.getLand();

        FlagContainer flagContainer = FlagContainer.EMPTY;
        Land land = new Land(null, owner, home, Collections.emptyList(), flagContainer);

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(toEntity(land));
            session.persist(toEntity(flagContainer.withLand(land)));
            session.persist(toEntity(home));

            addLandArea(land, "default", List.of(chunk));

            transaction.commit();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot create land!", e);
            if (transaction != null) transaction.rollback();
        }

        return land;
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
    public @Nullable Land getLand(@NotNull LandPlayer landPlayer) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            var query = session.createQuery("SELECT l FROM Land l JOIN l.owner o JOIN FETCH l.chunks WHERE o.uuid = :uuid", LandEntity.class);
            return toModel(query.uniqueResult());
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot find any land players.", e);
            return null;
        }
    }

    private FlagContainerEntity toEntity(FlagContainer flagContainer) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(FlagContainerMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);
        return (FlagContainerEntity) mappingContext.doMapping(flagContainer);
    }

    private Land toModel(LandEntity land) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.ENTITY_TO_MODEL);
        return (Land) mappingContext.doMapping(land);
    }

    private LandEntity toEntity(Land land) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);
        return (LandEntity) mappingContext.doMapping(land);
    }

    private HomePositionEntity toEntity(HomePosition homePosition) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(HomePositionMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);
        return (HomePositionEntity) mappingContext.doMapping(homePosition);
    }

    private void removeFlagsFromLand(Land land) {
        var flagContainer = land.getFlagContainer();
        flagContainer.getEntityCapFlags().forEach(flag -> pandorasCluster.getLandFlagService().removeLandEntityCapFlag(flag, flagContainer));
        flagContainer.getRoleFlags().forEach(roleFlag -> pandorasCluster.getLandFlagService().removeLandRoleFlag(roleFlag, flagContainer));
        flagContainer.getNaturalFlags().forEach(naturalFlag -> pandorasCluster.getLandFlagService().removeLandNaturalFlag(naturalFlag, flagContainer));
    }
}
