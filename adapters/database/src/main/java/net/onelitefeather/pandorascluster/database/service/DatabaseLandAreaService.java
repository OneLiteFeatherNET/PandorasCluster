package net.onelitefeather.pandorascluster.database.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandAreaService;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.ClaimedChunkMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.land.LandAreaMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DatabaseLandAreaService implements LandAreaService {

    private final PandorasCluster pandorasCluster;
    private final DatabaseService databaseService;

    private final LoadingCache<ClaimedChunk, LandArea> landAreaCache = Caffeine
            .newBuilder()
            .maximumSize(500)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(this::getLandArea);


    public DatabaseLandAreaService(PandorasCluster pandorasCluster, DatabaseService databaseService) {
        this.pandorasCluster = pandorasCluster;
        this.databaseService = databaseService;
    }

    @Override
    public void claimChunk(@NotNull ClaimedChunk chunk, @Nullable LandArea landArea) {

        var chunkEntity = new ClaimedChunkEntity(null, chunk.getChunkIndex(), null);
        Transaction transaction = null;
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();
            session.merge(chunkEntity);
            transaction.commit();
            if (landArea != null) refreshCache(landArea);
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot claim chunk.", e);
        }
    }

    @Override
    public boolean removeClaimedChunk(long chunkIndex) {

        var claimedChunk = getClaimedChunk(chunkIndex);
        if (claimedChunk == null) return false;

        Transaction transaction = null;

        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();
            session.remove(toEntity(claimedChunk));
            transaction.commit();

        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot delete claimed chunk", e);
        }

        return false;
    }

    @Override
    public @Nullable ClaimedChunk getClaimedChunk(long chunkIndex) {
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            var query = session.createQuery("SELECT cc FROM ClaimedChunkEntity cc WHERE cc.chunkIndex = :chunkIndex", ClaimedChunkEntity.class);
            return toModel(query.uniqueResult());
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Could not find any chunk with chunkIndex %s".formatted(chunkIndex), e);
            return null;
        }
    }

    @Override
    public @Nullable LandArea getLandArea(long chunkIndex) {
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            var query = session.createQuery("SELECT cc FROM ClaimedChunkEntity cc JOIN FETCH cc.landArea WHERE cc.chunkIndex = :chunkIndex", ClaimedChunkEntity.class);

            LandAreaEntity landArea = (LandAreaEntity) query.uniqueResult().landArea();
            return toModel(landArea);

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Could not find any land area for the given chunk. (%s)".formatted(chunkIndex), e);
            return null;
        }
    }

    @Override
    public void unclaimArea(LandArea landArea) {
        landArea.getMembers().forEach(this.pandorasCluster.getLandPlayerService()::removeLandMember);
        landArea.getChunks().stream().map(ClaimedChunk::getChunkIndex).forEach(this::removeClaimedChunk);
        refreshCache(landArea);
    }

    private void refreshCache(LandArea landArea) {
        landArea.getChunks().forEach(this.landAreaCache::invalidate);
    }

    public ClaimedChunkEntity toEntity(ClaimedChunk chunk) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(ClaimedChunkMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);
        return (ClaimedChunkEntity) mappingContext.doMapping(chunk);
    }

    public ClaimedChunk toModel(ClaimedChunkEntity chunk) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(ClaimedChunkMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.ENTITY_TO_MODEL);
        return (ClaimedChunk) mappingContext.doMapping(chunk);
    }

    public LandArea toModel(LandAreaEntity landArea) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandAreaMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.ENTITY_TO_MODEL);
        return (LandArea) mappingContext.doMapping(landArea);
    }
}
