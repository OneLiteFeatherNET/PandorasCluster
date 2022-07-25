package net.onelitefeather.pandorascluster.service.services;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import org.bukkit.entity.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class LandPlayerService {

    private final PandorasClusterApi pandorasClusterApi;
    private final List<LandPlayer> landPlayers;

    public LandPlayerService(@NotNull PandorasClusterApi pandorasClusterApi) {
        this.pandorasClusterApi = pandorasClusterApi;
        this.landPlayers = new ArrayList<>();
    }

    public List<LandPlayer> getPlayers() {
        return this.landPlayers;
    }

    @Nullable
    public LandPlayer getLandPlayer(@NotNull UUID uuid) {
        for (int i = 0; i < this.landPlayers.size(); i++) {
            LandPlayer player = this.landPlayers.get(i);
            if (player.getUniqueId().equals(uuid)) {
                return player;
            }
        }

        return null;
    }

    @Nullable
    public LandPlayer getLandPlayer(@NotNull String name) {
        LandPlayer landPlayer = null;
        List<LandPlayer> players = this.landPlayers;
        for (int i = 0; i < players.size() && landPlayer == null; i++) {
            LandPlayer player = players.get(i);
            if (player.getName().equalsIgnoreCase(name)) {
                landPlayer = player;
            }
        }

        return null;
    }

    public void load() {
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            session.beginTransaction();
            var query = session.createQuery("SELECT lp FROM LandPlayer lp", LandPlayer.class);
            this.landPlayers.addAll(query.list());
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Could not load players.", e);
        }
    }

    public void createPlayer(@NotNull UUID uuid, @NotNull String name, Consumer<Boolean> consumer) {
        playerExists(uuid, exists -> {
            if (Boolean.FALSE.equals(exists)) {
                LandPlayer landPlayer = new LandPlayer(uuid, name);
                this.landPlayers.add(landPlayer);
                updateLandPlayer(landPlayer);
            }

            consumer.accept(!exists);
        });
    }

    public boolean deletePlayer(@NotNull UUID uuid) {

        LandPlayer landPlayer = getLandPlayer(uuid);
        if (landPlayer == null) return false;
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(landPlayer);
            session.getTransaction().commit();
            this.landPlayers.remove(landPlayer);
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Could not delete player data for %s", uuid), e);
        }

        return true;
    }

    @Nullable
    public LandPlayer fromDatabase(@NotNull UUID uuid) {
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var chunkPlayerQuery = session.createQuery("SELECT lp FROM LandPlayer lp WHERE lp.uuid = :uuid", LandPlayer.class);
            chunkPlayerQuery.setMaxResults(1);
            chunkPlayerQuery.setParameter("uuid", uuid.toString());
            return chunkPlayerQuery.getSingleResult();
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Could not load player data for %s", uuid), e);
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
