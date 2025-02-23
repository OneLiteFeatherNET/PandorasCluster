package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandPlayerService;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.impl.LandAreaMapper;
import net.onelitefeather.pandorascluster.database.mapper.impl.LandMemberMapper;
import net.onelitefeather.pandorascluster.database.models.player.LandPlayerEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseLandPlayerService implements LandPlayerService {

    private final DatabaseService databaseService;
    private final LandMemberMapper memberMapper;

    public DatabaseLandPlayerService(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.memberMapper = ((LandAreaMapper) databaseService.landAreaMapper()).getMemberMapper();
    }

    @Override
    public void addLandMember(@NotNull LandArea landArea, @NotNull LandPlayer member, @Nullable LandRole landRole) {

        LandRole role = landRole != null ? landRole : LandRole.VISITOR;
        var landMember = new LandMember(null, member, role);
        Transaction transaction = null;

        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();
            session.persist(this.memberMapper.modelToEntity(landMember));
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
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(this.memberMapper.modelToEntity(landMember));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot update land member %s".formatted(member.getMember().getName()), e);
        }
    }

    @Override
    public void removeLandMember(@NotNull LandMember member) {
        Transaction transaction = null;
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(this.memberMapper.modelToEntity(member));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove land member %s".formatted(member.getMember().getName()), e);
        }
    }

    @Override
    public @NotNull List<LandPlayer> getLandPlayers() {
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {
            var query = session.createQuery("SELECT lp FROM LandPlayerEntity lp", LandPlayerEntity.class);
            var players = query.list();
            return players.stream().map(this.databaseService.landPlayerMapper()::entityToModel).toList();
        } catch (HibernateException e) {
            Constants.LOGGER.log(Level.SEVERE, "Cannot find any land players.", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean createPlayer(@NotNull UUID uuid, @NotNull String name) {
        if (playerExists(uuid)) return false;

        Transaction transaction = null;

        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

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

        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();
            session.remove(databaseService.landPlayerMapper().modelToEntity(landPlayer));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE,
                    "Cannot delete land player with uuid %s and name %s".formatted(uuid.toString(), landPlayer.getName()), e);
        }
    }

    @Override
    public @Nullable LandPlayer getLandPlayer(@NotNull UUID uuid) {
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            var query = session.createQuery("SELECT lp FROM LandPlayerEntity lp WHERE lp.uuid = :uuid", LandPlayerEntity.class);
            return databaseService.landPlayerMapper().entityToModel(query.uniqueResult());
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
        try (SessionFactory factory = this.databaseService.sessionFactory();
             Session session = factory.openSession()) {

            transaction = session.beginTransaction();
            var landPlayerEntity = this.databaseService.landPlayerMapper().modelToEntity(landPlayer);
            session.merge(landPlayerEntity);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE,
                    "Cannot update land player with uuid %s and name %s".formatted(
                            landPlayer.getUniqueId().toString(),
                            landPlayer.getName()), e);
        }
    }
}
