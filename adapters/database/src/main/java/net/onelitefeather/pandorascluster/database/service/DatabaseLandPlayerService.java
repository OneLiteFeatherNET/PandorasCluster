package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.mapper.MapperStrategy;
import net.onelitefeather.pandorascluster.api.mapper.MappingContext;
import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandPlayerService;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.player.LandMemberMappingStrategy;
import net.onelitefeather.pandorascluster.database.mapper.player.LandPlayerMappingStrategy;
import net.onelitefeather.pandorascluster.database.models.player.LandMemberEntity;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class DatabaseLandPlayerService implements LandPlayerService {

    private final DatabaseService databaseService;

    public DatabaseLandPlayerService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void addLandMember(@NotNull LandArea landArea, @NotNull LandPlayer member, @Nullable LandRole landRole) {

        LandRole role = landRole != null ? landRole : LandRole.VISITOR;
        var landMember = new LandMember(null, member, role, landArea);
        Transaction transaction = null;

        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(toEntity(landMember));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot add land member %s with role %s".formatted(member.getName(), role), e);
        }
    }

    @Override
    public void updateLandMember(@NotNull LandArea landArea, @NotNull LandMember member) {

        var landMember = landArea.getMember(member.getMember().getUniqueId());
        if (landMember == null) return;

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(toEntity(member));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot update land member %s".formatted(member.getMember().getName()), e);
        }
    }

    @Override
    public void removeLandMember(@NotNull LandMember member) {
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(toEntity(member));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove land member %s".formatted(member.getMember().getName()), e);
        }
    }

    @Override
    public @NotNull List<LandPlayer> getLandPlayers() {
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            var query = session.createQuery("SELECT lp FROM LandPlayerEntity lp", LandPlayerEntity.class);
            var players = query.list();
            return players.stream().map(this::toModel).toList();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot find any land players.", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean createPlayer(@NotNull UUID uuid, @NotNull String name) {
        if (playerExists(uuid)) return false;

        Transaction transaction = null;

        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            LandPlayerEntity landPlayerEntity = new LandPlayerEntity(null, uuid.toString(), name);
            session.persist(landPlayerEntity);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot create land player with uuid %s and name %s".formatted(uuid.toString(), name), e);
            return false;
        }

        return true;
    }

    @Override
    public void deletePlayer(@NotNull UUID uuid) {

        var landPlayer = getLandPlayer(uuid);
        if (landPlayer == null) return;

        Transaction transaction = null;

        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            session.remove(toEntity(landPlayer));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE,
                    "Cannot delete land player with uuid %s and name %s".formatted(uuid.toString(), landPlayer.getName()), e);
        }
    }

    @Override
    public @Nullable LandPlayer getLandPlayer(@NotNull UUID uuid) {
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            var query = session.createQuery("SELECT lp FROM LandPlayerEntity lp WHERE lp.uuid = :uuid", LandPlayerEntity.class);
            query.setParameter("uuid", uuid.toString());
            return toModel(query.uniqueResult());
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot find land player for uuid %s.".formatted(uuid.toString()), e);
            return null;
        }
    }

    @Override
    public boolean playerExists(@NotNull UUID uuid) {
        return getLandPlayer(uuid) != null;
    }

    @Override
    public void updateLandPlayer(@NotNull LandPlayer landPlayer) {
        if (!playerExists(landPlayer.getUniqueId())) return;
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            session.merge(toEntity(landPlayer));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE,
                    "Cannot update land player with uuid %s and name %s".formatted(
                            landPlayer.getUniqueId().toString(),
                            landPlayer.getName()), e);
        }
    }

    private LandPlayer toModel(@NotNull LandPlayerEntity entity) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandPlayerMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.ENTITY_TO_MODEL);
        return (LandPlayer) mappingContext.doMapping(entity);
    }

    private LandPlayerEntity toEntity(@NotNull LandPlayer player) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandPlayerMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);
        return (LandPlayerEntity) mappingContext.doMapping(player);
    }

    private LandMemberEntity toEntity(@NotNull LandMember member) {
        MappingContext mappingContext = MappingContext.create();
        mappingContext.setMappingStrategy(LandMemberMappingStrategy.create());
        mappingContext.setMappingType(MapperStrategy.MapperType.MODEL_TO_ENTITY);
        return (LandMemberEntity) mappingContext.doMapping(member);
    }
}
