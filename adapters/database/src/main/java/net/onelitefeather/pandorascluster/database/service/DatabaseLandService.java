package net.onelitefeather.pandorascluster.database.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.position.HomePosition;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandService;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.ClaimedChunkMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.impl.LandAreaMapper;
import net.onelitefeather.pandorascluster.database.mapper.impl.LandMapper;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DatabaseLandService implements LandService {

    private final PandorasCluster pandorasCluster;
    private final DatabaseService databaseService;
    private final LandMapper landMapper;
    private final LandAreaMapper landAreaMapper;

    private final LoadingCache<ClaimedChunk, LandArea> landAreaCache = Caffeine
            .newBuilder()
            .maximumSize(500)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(this::getLandArea);

    public DatabaseLandService(PandorasCluster cluster) {
        this.pandorasCluster = cluster;
        this.databaseService = cluster.getDatabaseService();
        this.landAreaMapper = (LandAreaMapper) databaseService.landAreaMapper();
        this.landMapper = (LandMapper) databaseService.landMapper();
    }

    @Override
    public @NotNull List<Land> getLands() {
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            var query = session.createQuery("SELECT l FROM LandEntity l", LandEntity.class);
            var players = query.list();
            return players.stream().map(this.databaseService.landMapper()::entityToModel).toList();
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
            var homeEntity = this.landMapper.getHomePositionMapper().modelToEntity(homePosition);
            session.merge(homeEntity);
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
            var landEntity = this.landMapper.modelToEntity(land);
            session.merge(landEntity);
            transaction.commit();
            refreshCache(land);
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot update land!", e);
        }
    }

    @Override
    public void claimChunk(@NotNull ClaimedChunk chunk, @Nullable LandArea landArea) {

        var chunkEntity = new ClaimedChunkEntity(null, chunk.getChunkIndex(), null);
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            session.merge(chunkEntity);
            transaction.commit();
            if (landArea != null) updateLand(landArea.getLand());
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

            var landAreaEntity = this.databaseService.landAreaMapper().modelToEntity(landArea);
            session.persist(landAreaEntity);
            MappingContext mappingContext = MappingContext.create();
            mappingContext.setMappingStrategy(ClaimedChunkMappingStrategy.create());
            mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);
            landArea.getChunks().stream().map(mappingContext::<ClaimedChunk, ClaimedChunkEntity>doMapping).forEach(session::persist);

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
        }
    }

    @Override
    public @Nullable Land createLand(@NotNull LandPlayer owner, @NotNull HomePosition home, @NotNull ClaimedChunk chunk, @NotNull String world) {

        var landArea = getLandArea(chunk);
        if (landArea != null) return landArea.getLand();


        //TODO: Add flagContainer
        Land land = new Land(null, owner, home, Collections.emptyList(), null);

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(this.landMapper.getHomePositionMapper().modelToEntity(home));
            session.persist(this.landMapper.modelToEntity(land));
            addLandArea(land, "default", List.of(chunk));
            transaction.commit();
        } catch (HibernateException e) {
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
            land.getAreas().forEach(this::unclaimLandArea);

            session.remove(land);
            session.remove(this.landMapper.getHomePositionMapper().modelToEntity(land.getHome()));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
        } finally {
            refreshCache(land);
        }
    }

    @Override
    public boolean removeClaimedChunk(long chunkIndex) {

        return false;
    }

    @Override
    public @Nullable LandArea getLandArea(long chunkIndex) {
        return null;
    }

    @Override
    public @Nullable Land getLand(@NotNull LandPlayer landPlayer) {
        return null;
    }

    @Override
    public boolean isChunkClaimed(long chunkIndex) {
        return false;
    }

    @Override
    public @Nullable ClaimedChunk getClaimedChunk(long chunkIndex) {
        return null;
    }

    private void unclaimLandArea(LandArea landArea) {
        landArea.getMembers().forEach(this.pandorasCluster.getLandPlayerService()::removeLandMember);
        landArea.getChunks().stream().map(ClaimedChunk::getChunkIndex).forEach(this::removeClaimedChunk);
    }

    private void refreshCache(Land land) {
        land.getAreas().forEach(landArea -> this.landAreaCache.invalidateAll(landArea.getChunks()));
    }

    private void removeFlagsFromLand(Land land) {
        var flagContainer = land.getFlagContainer();

        flagContainer.getEntityCapFlags().forEach(flag -> pandorasCluster.getLandFlagService().removeLandEntityCapFlag(flag, land));
        flagContainer.getRoleFlags().forEach(roleFlag -> pandorasCluster.getLandFlagService().removeLandRoleFlag(roleFlag, land));
        flagContainer.getNaturalFlags().forEach(naturalFlag -> pandorasCluster.getLandFlagService().removeLandNaturalFlag(naturalFlag, land));
    }
}
