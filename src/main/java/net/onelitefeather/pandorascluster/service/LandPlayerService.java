package net.onelitefeather.pandorascluster.service;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class LandPlayerService {

    public static final LandPlayer DUMMY = new LandPlayer(UUID.randomUUID(), "Steve");
    private final PandorasClusterApi pandorasClusterApi;

    public LandPlayerService(@NotNull PandorasClusterApi pandorasClusterApi) {
        this.pandorasClusterApi = pandorasClusterApi;
    }

    public List<LandPlayer> getPlayers() {
        List<LandPlayer> landPlayers = new ArrayList<>();
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var query = session.createQuery("SELECT lp FROM LandPlayer lp", LandPlayer.class);
            landPlayers.addAll(query.list());
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Could not load players.", e);
        }

        return landPlayers;
    }

    public void createPlayer(@NotNull UUID uuid, @NotNull String name, Consumer<Boolean> consumer) {
        playerExists(uuid, exists -> {
            if (Boolean.FALSE.equals(exists)) {
                LandPlayer landPlayer = new LandPlayer(uuid, name);
                updateLandPlayer(landPlayer);
            }

            consumer.accept(!exists);
        });
    }

    public boolean deletePlayer(@NotNull UUID uuid) {

        LandPlayer landPlayer = getLandPlayer(uuid);
        if (landPlayer == null) return false;

        Transaction transaction = null;

        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(landPlayer);
            transaction.commit();
        } catch (HibernateException e) {

            if (transaction != null) {
                transaction.rollback();
                this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Could not delete player data for %s", uuid), e);
            }
        }

        return true;
    }

    @Nullable
    public LandPlayer getLandPlayer(@NotNull UUID uuid) {
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var chunkPlayerQuery = session.createQuery("SELECT lp FROM LandPlayer lp WHERE lp.uuid = :uuid", LandPlayer.class);
            chunkPlayerQuery.setMaxResults(1);
            chunkPlayerQuery.setParameter("uuid", uuid.toString());
            return chunkPlayerQuery.uniqueResult();
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Could not load player data for %s", uuid), e);
        }

        return null;
    }

    @Nullable
    public LandPlayer getLandPlayer(@NotNull String name) {
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var chunkPlayerQuery = session.createQuery("SELECT lp FROM LandPlayer lp WHERE lp.name = :name", LandPlayer.class);
            chunkPlayerQuery.setMaxResults(1);
            chunkPlayerQuery.setParameter("name", name);
            return chunkPlayerQuery.uniqueResult();
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Could not load player data for %s", name), e);
        }

        return null;
    }

    public void playerExists(@NotNull UUID uuid, @NotNull Consumer<Boolean> consumer) {

        boolean exists = false;
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var chunkPlayerQuery = session.createQuery("SELECT 1 FROM LandPlayer lp WHERE lp.uuid = :uuid", LandPlayer.class);
            chunkPlayerQuery.setParameter("uuid", uuid.toString());
            exists = chunkPlayerQuery.uniqueResult() != null;
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Could not load player data for %s", uuid), e);
        }

        consumer.accept(exists);
    }

    public void updateLandPlayer(@NotNull LandPlayer chunkPlayer) {
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(chunkPlayer);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Could not update player data %s", chunkPlayer.getUniqueId()), e);
        }
    }
}
