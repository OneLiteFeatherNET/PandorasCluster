package net.onelitefeather.pandorascluster.service;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.builder.LandBuilder;
import net.onelitefeather.pandorascluster.enums.ChunkRotation;
import net.onelitefeather.pandorascluster.land.ChunkPlaceholder;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.land.position.HomePosition;
import net.onelitefeather.pandorascluster.service.services.LandFlagService;
import net.onelitefeather.pandorascluster.util.ChunkUtil;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

public class LandService {

    private final PandorasClusterApi pandorasClusterApi;
    private final Map<OfflinePlayer, Land> playerLands;

    private final Map<Chunk, Land> claimedChunks;


    public LandService(PandorasClusterApi pandorasClusterApi) {
        this.pandorasClusterApi = pandorasClusterApi;
        this.playerLands = new HashMap<>();
        this.claimedChunks = new HashMap<>();

        PluginManager pluginManager = pandorasClusterApi.getPlugin().getServer().getPluginManager();

        LandFlagService landFlagService = this.pandorasClusterApi.getLandFlagService();
        pluginManager.registerEvents(new LandBlockListener(this, landFlagService), pandorasClusterApi.getPlugin());
        pluginManager.registerEvents(new LandEntityListener(this, landFlagService), pandorasClusterApi.getPlugin());
        pluginManager.registerEvents(new LandPlayerListener(this, landFlagService), pandorasClusterApi.getPlugin());
        pluginManager.registerEvents(new LandWorldListener(this, landFlagService), pandorasClusterApi.getPlugin());
    }

    public void load() {

        Server server = this.pandorasClusterApi.getPlugin().getServer();

        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            session.beginTransaction();
            var query = session.createQuery("SELECT kd FROM Land kd", Land.class);

            for (Land land : query.list()) {

                OfflinePlayer offlinePlayer = server.getOfflinePlayer(land.getOwner().getUniqueId());
                if (!offlinePlayer.hasPlayedBefore() || this.playerLands.containsKey(offlinePlayer)) continue;
                this.playerLands.put(offlinePlayer, land);

                World world = server.getWorld(land.getWorld());
                if (world == null) continue;

                for (int i = 0; i < land.getMergedChunks().size(); i++) {
                    ChunkPlaceholder mergedChunk = land.getMergedChunks().get(i);

                    int chunkX = ChunkUtil.getChunkCoordX(mergedChunk.getChunkIndex());
                    int chunkZ = ChunkUtil.getChunkCoordZ(mergedChunk.getChunkIndex());
                    this.claimedChunks.put(world.getChunkAt(chunkX, chunkZ), land);
                }

                Chunk chunk = world.getChunkAt(land.getX(), land.getZ());
                this.claimedChunks.put(chunk, land);
            }

        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Could not load lands.", e);
        }
    }

    public boolean hasPlayerLand(@NotNull UUID playerId) {
        OfflinePlayer offlinePlayer = this.pandorasClusterApi.getPlugin().getServer().getOfflinePlayer(playerId);
        if (!offlinePlayer.hasPlayedBefore()) return false;
        return this.playerLands.containsKey(offlinePlayer);
    }

    @Nullable
    private Land getPlayerLand(@NotNull UUID uniqueId) {
        OfflinePlayer offlinePlayer = this.pandorasClusterApi.getPlugin().getServer().getOfflinePlayer(uniqueId);
        if (!offlinePlayer.hasPlayedBefore()) return null;
        return this.playerLands.get(offlinePlayer);
    }

    @NotNull
    public Map<OfflinePlayer, Land> getPlayerLands() {
        return playerLands;
    }

    @Nullable
    public Land getLand(@NotNull LandPlayer owner) {

        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var landOfOwner = session.createQuery("SELECT l FROM Land l JOIN l.owner o WHERE o.uuid = :uuid", Land.class);
            landOfOwner.setParameter("uuid", owner.getUniqueId().toString());
            return landOfOwner.getSingleResult();
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update land", e);
        }

        return null;
    }

    @Nullable
    public HomePosition getHome(@NotNull UUID uuid) {
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var landOfOwner = session.createQuery("SELECT h FROM Land l JOIN l.homePosition h JOIN l.owner p WHERE p.uuid = :uuid", HomePosition.class);
            landOfOwner.setParameter("uuid", uuid.toString());
            return landOfOwner.getSingleResult();
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update land", e);
        }

        return null;
    }

    public void createLand(@NotNull LandPlayer owner, @NotNull Player player, @NotNull Chunk chunk) {

        if (!this.getPlayerLands().containsKey(player)) {

            LandPlayer fromDatabase = this.pandorasClusterApi.getLandPlayerService().fromDatabase(player.getUniqueId());
            if (fromDatabase != null) {
                owner = fromDatabase;
            }

            Transaction transaction = null;

            try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();

                HomePosition homePosition = HomePosition.of(player.getLocation());
                session.persist(homePosition);

                Land land = new LandBuilder().
                        owner(owner).
                        home(homePosition).
                        world(player.getWorld()).
                        chunkX(chunk.getX()).
                        chunkZ(chunk.getZ()).
                        members(List.of()).
                        mergedChunks(List.of()).
                        build();

                session.persist(land);

                this.pandorasClusterApi.getLandFlagService().addFlags(land);

                transaction.commit();

                this.playerLands.put(player, land);
                this.claimedChunks.put(chunk, land);
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update land", e);
            }
        }
    }

    public void deletePlayerLand(@NotNull Player player) {

        Land land = this.playerLands.remove(player);

        World world = player.getServer().getWorld(land.getWorld());
        if (world == null) return;

        Chunk chunk = world.getChunkAt(land.getX(), land.getZ());
        this.claimedChunks.remove(chunk);

        if (exists(land)) {
            Transaction transaction = null;
            try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.remove(land);
                transaction.commit();

                this.playerLands.remove(player);

                Map<Chunk, Land> toRemove = new HashMap<>();
                for (Map.Entry<Chunk, Land> landEntry : this.claimedChunks.entrySet()) {
                    if (landEntry.getValue().equals(land)) {
                        toRemove.put(landEntry.getKey(), land);
                    }
                }

                toRemove.forEach(this.claimedChunks::remove);

            } catch (HibernateException e) {

                if (transaction != null) {
                    transaction.rollback();
                }

                this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot remove the land from the database.", e);
            }
        }
    }

    public boolean exists(@NotNull Land land) {
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var kitCooldown = session.createQuery("SELECT kdc FROM Land kdc WHERE playerId = :playerId AND id = :id", Land.class);
            kitCooldown.setMaxResults(1);
            kitCooldown.setParameter("playerId", land.getOwner().getUniqueId().toString());
            kitCooldown.setParameter("id", land.getId());
            return kitCooldown.uniqueResult() != null;
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Something went wrong!", e);
            return false;
        }
    }

    public boolean isChunkClaimed(@NotNull Chunk chunk) {
        return this.claimedChunks.containsKey(chunk);
    }

    public void findConnectedChunk(@NotNull Player player, @NotNull Consumer<Land> consumer) {


        ChunkRotation chunkRotation = ChunkRotation.getChunkRotation(player.getFacing().name());
        if (chunkRotation != null) {

            Chunk chunk = player.getChunk();
            Chunk connectedChunk = player.getWorld().getChunkAt(
                    chunk.getX() + chunkRotation.getX(),
                    chunk.getZ() + chunkRotation.getZ());

            Land land = this.getLand(connectedChunk);
            consumer.accept(land);
        }
    }

    public boolean isChunkMerged(@NotNull Chunk chunk) {
        boolean merged = false;
        Iterator<Chunk> iterator = this.claimedChunks.keySet().iterator();
        while (iterator.hasNext() && !merged) {
            Land land = this.claimedChunks.get(iterator.next());
            merged = land.getMergedChunk(ChunkUtil.getChunkIndex(chunk)) != null;
        }

        return merged;
    }

    @Nullable
    public Land getLand(@NotNull Chunk chunk) {
        return this.claimedChunks.get(chunk);
    }

    public void merge(@NotNull Land base, @NotNull Chunk chunk) {
        base.mergeChunk(chunk);
        this.claimedChunks.put(chunk, base);
        updatePlayerLand(base);
    }

    public void updatePlayerLand(@NotNull Land land) {
        Transaction transaction = null;
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            for (ChunkPlaceholder chunkPlaceholder : land.getMergedChunks()) {
                if (!isMergedChunkStored(land, chunkPlaceholder)) {
                    session.persist(chunkPlaceholder);
                }
            }

            session.merge(land);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Cannot update worldchunk %s", land), e);
        }
    }

    public boolean hasSameOwner(@NotNull Land land, @NotNull Land other) {
        return land.getOwner().equals(other.getOwner());
    }

    private boolean isMergedChunkStored(Land land, ChunkPlaceholder chunkPlaceholder) {

        boolean stored = false;

        // SELECT h FROM Land l JOIN l.homePosition h JOIN l.owner p WHERE p.uuid = :uuid


        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            var query = session.createQuery("SELECT * FROM Land_ChunkPlaceholder JOIN cph ON lcph.chunks_id = cph.id WHERE Land_id = :landId AND chunks_id = :chunkId", ChunkPlaceholder.class);
            query.setParameter("landId", land.getId());
            query.setParameter("chunkId", chunkPlaceholder.getId());
            stored = !query.list().isEmpty();
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, "Cannot update land", e);
        }

        return stored;
    }
}

