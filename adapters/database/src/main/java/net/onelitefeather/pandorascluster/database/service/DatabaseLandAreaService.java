package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.chunk.ClaimedChunk;
import net.onelitefeather.pandorascluster.api.land.Land;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandAreaService;
import net.onelitefeather.pandorascluster.api.service.result.land.*;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.ClaimedChunkMapper;
import net.onelitefeather.pandorascluster.database.mapper.land.LandAreaMapper;
import net.onelitefeather.pandorascluster.database.mapper.land.LandMapper;
import net.onelitefeather.pandorascluster.database.mapper.player.LandMemberMapper;
import net.onelitefeather.pandorascluster.database.models.chunk.ClaimedChunkEntity;
import net.onelitefeather.pandorascluster.database.models.land.LandAreaEntity;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Level;

public record DatabaseLandAreaService(DatabaseService databaseService) implements LandAreaService {

    @Override
    public void claimChunk(@Nullable Long id, @NotNull ClaimedChunk chunk, @Nullable LandArea landArea) {
        var chunkEntity = new ClaimedChunkEntity(id, chunk.chunkIndex(), LandAreaMapper.toEntity(landArea));
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(chunkEntity);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot claim chunk.", e);
        }
    }

    @Override
    public UnclaimChunkResult removeClaimedChunk(long chunkIndex) {

        if (!(getClaimedChunk(chunkIndex) instanceof GetClaimedChunkResult.Found(ClaimedChunk claimedChunk)))
            return new UnclaimChunkResult.NotFound();

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            session.remove(ClaimedChunkMapper.toEntity(claimedChunk));
            transaction.commit();
            return new UnclaimChunkResult.Success(claimedChunk);

        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            var result = new UnclaimChunkResult.Failed("Failed to unclaim chunk: %d".formatted(chunkIndex), e);
            Constants.LOGGER.log(Level.SEVERE, result.message(), result.cause());
            return result;
        }
    }

    @Override
    public @NotNull GetClaimedChunkResult getClaimedChunk(long chunkIndex) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            var query = session.createQuery("SELECT cc FROM ClaimedChunkEntity cc WHERE cc.chunkIndex = :chunkIndex", ClaimedChunkEntity.class);
            query.setParameter("chunkIndex", chunkIndex);
            ClaimedChunkEntity row = query.uniqueResult();
            if (row == null) return new GetClaimedChunkResult.NotFound();
            return new GetClaimedChunkResult.Found(ClaimedChunkMapper.toModel(row));
        } catch (HibernateException e) {
            var result = new GetClaimedChunkResult.Failed("Could not find any chunk with chunkIndex %s".formatted(chunkIndex), e);
            Constants.LOGGER.log(Level.SEVERE, result.message(), e);
            return result;
        }
    }


    @Override
    public @NotNull GetLandAreaResult getLandArea(String name, long id) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            var chunkQuery = session.createQuery(
                    "SELECT la FROM LandAreaEntity la " +
                            "LEFT JOIN FETCH la.land " +
                            "WHERE la.name = :name AND la.id = :id",
                    LandAreaEntity.class);
            chunkQuery.setParameter("name", name);
            chunkQuery.setParameter("id", id);

            var result = chunkQuery.uniqueResult();
            if (result == null) return new GetLandAreaResult.NotFound();

            return new GetLandAreaResult.Found(LandAreaMapper.toModel(result));
        } catch (HibernateException e) {
            var result = new GetLandAreaResult.Failed("Could not find any land area for the given chunk. (%s)".formatted(name), e);
            Constants.LOGGER.log(Level.SEVERE, result.message(), e);
            return result;
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

            return getLandAreaResult(chunkQuery);
        } catch (HibernateException e) {
            var result = new GetLandAreaResult.Failed("Could not find any land area for the given chunk. (%s)".formatted(chunkIndex), e);
            Constants.LOGGER.log(Level.SEVERE, result.message(), e);
            return result;
        }
    }

    @Override
    public DeleteLandAreaResult unclaimArea(LandArea landArea) {

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            landArea.members().stream().map(LandMemberMapper::toEntity).forEach(session::remove);
            landArea.chunks().stream().map(ClaimedChunkMapper::toEntity).forEach(session::remove);
            session.remove(LandAreaMapper.toEntity(landArea));
            transaction.commit();
            return new DeleteLandAreaResult.Success(landArea);
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            var result = new DeleteLandAreaResult.Failed("Failed to unclaim land area with name %s for land with id %s"
                    .formatted(landArea.name(), landArea.landId()), e);
            Constants.LOGGER.log(Level.SEVERE, result.message(), e);
            return result;
        }
    }

    @Override
    public CreateLandAreaResult createArea(Land land, String name, LandArea area, ClaimedChunk chunk) {

        var landArea = new LandArea(null, land.id(), name, List.of(chunk), area.members());

        if (getLandArea(chunk.chunkIndex()) instanceof GetLandAreaResult.Found(LandArea found)
                && !found.name().equalsIgnoreCase("default")
                && !found.name().equalsIgnoreCase(name)) {
            return new CreateLandAreaResult.AlreadyAssigned(found.name());
        }

        if (getLandArea(name, area.id()) instanceof GetLandAreaResult.Found(LandArea found))
            return new CreateLandAreaResult.AlreadyCreated(found.name());

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();

            var landAreaEntity = LandAreaMapper.toEntity(landArea);
            landAreaEntity.setLand(LandMapper.toEntity(land));

            session.persist(landAreaEntity);
            session.merge(new ClaimedChunkEntity(chunk.id(), chunk.chunkIndex(), landAreaEntity));
            transaction.commit();

            return new CreateLandAreaResult.Created(landArea);

        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();

            var result = new CreateLandAreaResult.Failed("Failed to create land area with name %s for land with id %s"
                    .formatted(name, land.id()), e);

            Constants.LOGGER.log(Level.SEVERE, result.message(), e);
            return result;
        }
    }

    @NotNull
    private GetLandAreaResult getLandAreaResult(Query<ClaimedChunkEntity> chunkQuery) {
        ClaimedChunkEntity claimedChunk = chunkQuery.uniqueResult();
        if (claimedChunk == null) return new GetLandAreaResult.NotFound();

        LandAreaEntity landArea = claimedChunk.landArea();
        Hibernate.initialize(landArea.members());
        Hibernate.initialize(landArea.chunks());

        return new GetLandAreaResult.Found(LandAreaMapper.toModel(landArea));
    }
}
