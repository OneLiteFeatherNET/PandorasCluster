package net.onelitefeather.pandorascluster.database.service;

import net.onelitefeather.pandorascluster.api.enums.LandRole;
import net.onelitefeather.pandorascluster.api.land.LandArea;
import net.onelitefeather.pandorascluster.api.player.LandMember;
import net.onelitefeather.pandorascluster.api.player.LandPlayer;
import net.onelitefeather.pandorascluster.api.service.DatabaseService;
import net.onelitefeather.pandorascluster.api.service.LandPlayerService;
import net.onelitefeather.pandorascluster.api.service.result.player.AddLandMemberResult;
import net.onelitefeather.pandorascluster.api.service.result.player.CreateLandPlayerResult;
import net.onelitefeather.pandorascluster.api.service.result.player.DeleteLandPlayerResult;
import net.onelitefeather.pandorascluster.api.service.result.player.RemoveLandMemberResult;
import net.onelitefeather.pandorascluster.api.util.Constants;
import net.onelitefeather.pandorascluster.database.mapper.player.LandMemberMapper;
import net.onelitefeather.pandorascluster.database.mapper.player.LandPlayerMapper;
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

public record DatabaseLandPlayerService(DatabaseService databaseService) implements LandPlayerService {

    @Override
    public AddLandMemberResult addLandMember(@NotNull LandArea landArea, @NotNull LandPlayer member, @Nullable LandRole landRole) {

        LandRole role = landRole != null ? landRole : LandRole.VISITOR;
        var landMember = new LandMember(null, member, role);
        Transaction transaction = null;

        if (landArea.getMember(member.uniqueId()) != null)
            return new AddLandMemberResult.AlreadyAdded("Player is already a member of this land area.");

        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(toEntity(landMember));
            transaction.commit();
            return new AddLandMemberResult.Added(landMember);
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot add land member %s with role %s".formatted(member.name(), role), e);
            return new AddLandMemberResult.Failed("Cannot add land member", e);
        }
    }

    @Override
    public void updateLandMember(@NotNull LandArea landArea, @NotNull LandMember member) {

        var landMember = landArea.getMember(member.member().uniqueId());
        if (landMember == null) return;

        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(toEntity(member));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot update land member %s".formatted(member.member().name()), e);
        }
    }

    @Override
    public RemoveLandMemberResult removeLandMember(@NotNull LandMember member) {
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(toEntity(member));
            transaction.commit();
            return new RemoveLandMemberResult.Removed(member);
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot remove land member %s".formatted(member.member().name()), e);
            return new RemoveLandMemberResult.Failed("Cannot remove land member", e);
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
    public CreateLandPlayerResult createPlayer(@NotNull UUID uuid, @NotNull String name) {
        if (getLandPlayer(uuid) != null)
            return new CreateLandPlayerResult.AlreadyCreated("A land player with uuid %s already exists.".formatted(uuid.toString()));
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {
            transaction = session.beginTransaction();
            LandPlayerEntity landPlayerEntity = new LandPlayerEntity(null, uuid.toString(), name);
            session.persist(landPlayerEntity);
            transaction.commit();
            return new CreateLandPlayerResult.Created(uuid, name);
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE, "Cannot create land player with uuid %s and name %s".formatted(uuid.toString(), name), e);
            return new CreateLandPlayerResult.Failed("Cannot create land player", e);
        }
    }

    @Override
    public DeleteLandPlayerResult deletePlayer(@NotNull UUID uuid) {

        var landPlayer = getLandPlayer(uuid);
        if (landPlayer == null) return new DeleteLandPlayerResult.NotFound();

        Transaction transaction = null;

        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            session.remove(toEntity(landPlayer));
            transaction.commit();
            return new DeleteLandPlayerResult.Deleted(landPlayer);
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE,
                    "Cannot delete land player with uuid %s and name %s".formatted(uuid.toString(), landPlayer.name()), e);
            return new DeleteLandPlayerResult.Failed("Cannot delete land player", e);
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
        if (!playerExists(landPlayer.uniqueId())) return;
        Transaction transaction = null;
        try (Session session = this.databaseService.sessionFactory().openSession()) {

            transaction = session.beginTransaction();
            session.merge(toEntity(landPlayer));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            Constants.LOGGER.log(Level.SEVERE,
                    "Cannot update land player with uuid %s and name %s".formatted(
                            landPlayer.uniqueId().toString(),
                            landPlayer.name()), e);
        }
    }

    private LandPlayer toModel(@NotNull LandPlayerEntity entity) {
        return LandPlayerMapper.toModel(entity);
    }

    private LandPlayerEntity toEntity(@NotNull LandPlayer player) {
        return LandPlayerMapper.toEntity(player);
    }

    private LandMemberEntity toEntity(@NotNull LandMember member) {
        return LandMemberMapper.toEntity(member);
    }
}
