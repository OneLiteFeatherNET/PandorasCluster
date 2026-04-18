package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.PandorasCluster;
import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.GetClaimedChunkResult;
import net.onelitefeather.pandorascluster.api.service.GetLandAreaResult;
import net.onelitefeather.pandorascluster.api.service.LandAreaService;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.ClaimedChunkMapper;
import net.onelitefeather.pandorascluster.database.mapper.land.LandAreaMapper;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public final class DatabaseLandAreaService implements LandAreaService {

    private final PandorasCluster pandorasCluster;
    private final DatabaseService databaseService;

    public DatabaseLandAreaService(PandorasCluster pandorasCluster, DatabaseService databaseService) {
        this.pandorasCluster = pandorasCluster;
        this.databaseService = databaseService;
    }

    @Override
    public void claimChunk(@NotNull ClaimedChunk chunk, @Nullable LandArea landArea) {

        var chunkEntity = new ClaimedChunkEntity(null, chunk.getChunkIndex(), null);
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

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

        if (!(getClaimedChunk(chunkIndex) instanceof GetClaimedChunkResult.Found(ClaimedChunk claimedChunk))) {
            return false;
        }

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            session.remove(toEntity(claimedChunk));
            transaction.commit();
            return true;

        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot delete claimed chunk", e);
            return false;
        }
    }

    @Override
    public @NotNull GetClaimedChunkResult getClaimedChunk(long chunkIndex) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            var query = session.createQuery("SELECT cc FROM ClaimedChunkEntity cc WHERE cc.chunkIndex = :chunkIndex", ClaimedChunkEntity.class);
            query.setParameter("chunkIndex", chunkIndex);
            ClaimedChunkEntity row = query.uniqueResult();
            if (row == null) return new GetClaimedChunkResult.NotFound();
            return new GetClaimedChunkResult.Found(toModel(row));
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Could not find any chunk with chunkIndex %s".formatted(chunkIndex), e);
            return new GetClaimedChunkResult.Failed("Could not find any chunk with chunkIndex %s".formatted(chunkIndex), e);
        }
    }

    @Override
    public @NotNull GetLandAreaResult getLandArea(long chunkIndex) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            var chunkQuery = session.createQuery(
                    "SELECT cc FROM ClaimedChunkEntity cc " +
                            "JOIN FETCH cc.landArea la " +
                            "LEFT JOIN FETCH la.land " +
                            "WHERE cc.chunkIndex = :chunkindex",
                    ClaimedChunkEntity.class);
            chunkQuery.setParameter("chunkindex", chunkIndex);

            ClaimedChunkEntity claimedChunk = chunkQuery.uniqueResult();
            if (claimedChunk == null) return new GetLandAreaResult.NotFound();

            LandAreaEntity landArea = (LandAreaEntity) claimedChunk.landArea();

            // Hibernate forbids JOIN FETCH-ing two bag-style collections in one query
            // (MultipleBagFetchException), so the members and chunks collections are
            // initialized in separate round-trips.
            Hibernate.initialize(landArea.members());
            Hibernate.initialize(landArea.chunks());

            return new GetLandAreaResult.Found(toModel(landArea));

        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Could not find any land area for the given chunk. (%s)".formatted(chunkIndex), e);
            return new GetLandAreaResult.Failed("Could not find any land area for the given chunk. (%s)".formatted(chunkIndex), e);
        }
    }

    /**
     * Best-effort composition of already-transactional sub-operations. Each call
     * to {@code removeLandMember} and {@code removeClaimedChunk} opens its own
     * Hibernate session and commits independently, so a partial failure leaves
     * the area in an intermediate state. Do not rely on atomicity here.
     */
    @Override
    public void unclaimArea(LandArea landArea) {
        landArea.getMembers().forEach(this.pandorasCluster.getLandPlayerService()::removeLandMember);
        landArea.getChunks().stream().map(ClaimedChunk::getChunkIndex).forEach(this::removeClaimedChunk);
        refreshCache(landArea);
    }

    private void refreshCache(LandArea landArea) {
        //TODO: Implement cache
//        landArea.getChunks().forEach(this.landAreaCache::invalidate);
    }

    public ClaimedChunkEntity toEntity(ClaimedChunk chunk) {
        return ClaimedChunkMapper.toEntity(chunk);
    }

    public ClaimedChunk toModel(ClaimedChunkEntity chunk) {
        return ClaimedChunkMapper.toModel(chunk);
    }

    public LandArea toModel(LandAreaEntity landArea) {
        return LandAreaMapper.toModel(landArea);
    }
}
