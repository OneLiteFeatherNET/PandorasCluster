package net.onelitefeather.pandorascluster.service;

import net.onelitefeather.pandorascluster.api.PandorasClusterApi;
import net.onelitefeather.pandorascluster.builder.LandBuilder;
import net.onelitefeather.pandorascluster.enums.ChunkRotation;
import net.onelitefeather.pandorascluster.land.Land;
import net.onelitefeather.pandorascluster.land.player.LandPlayer;
import net.onelitefeather.pandorascluster.util.ChunkUtil;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

        pluginManager.registerEvents(new LandBlockListener(this), pandorasClusterApi.getPlugin());
        pluginManager.registerEvents(new LandEntityListener(this), pandorasClusterApi.getPlugin());
        pluginManager.registerEvents(new LandPlayerListener(this), pandorasClusterApi.getPlugin());
        pluginManager.registerEvents(new LandWorldListener(this), pandorasClusterApi.getPlugin());
    }

    public void load() {

        Server server = this.pandorasClusterApi.getPlugin().getServer();

        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            session.beginTransaction();
            var query = session.createQuery("SELECT kd FROM Land kd", Land.class);

            for (Land land : query.list()) {

                OfflinePlayer offlinePlayer = server.getOfflinePlayer(land.getOwner().getUniqueId());
                if (!offlinePlayer.hasPlayedBefore()) continue;
                if (this.playerLands.containsKey(offlinePlayer)) continue;
                this.playerLands.put(offlinePlayer, land);

                for (int i = 0; i < land.getMergedChunks().size(); i++) {
                    long chunkIndex = land.getMergedChunks().get(i);

                    int chunkX = ChunkUtil.getChunkCoordX(chunkIndex);
                    int chunkZ = ChunkUtil.getChunkCoordZ(chunkIndex);

                    World world = server.getWorld(land.getWorld());
                    if (world == null) continue;
                    this.claimedChunks.put(world.getChunkAt(chunkX, chunkZ), land);
                }
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

    public void createLand(@NotNull LandPlayer owner, @NotNull Player player, @NotNull Chunk chunk) {

        Land land = new LandBuilder().
                owner(owner).
                home(player.getLocation()).
                world(player.getWorld()).
                chunkX(chunk.getX()).
                chunkZ(chunk.getZ()).
                members(List.of()).
                mergedChunks(List.of()).
                withFlags(List.of()).
                build();

        if (!this.getPlayerLands().containsKey(player)) {
            try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.persist(land);
                session.getTransaction().commit();
                this.playerLands.put(player, land);
                this.claimedChunks.put(chunk, land);
            } catch (HibernateException e) {
                this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Cannot update worldchunk %s", land.toString()), e);
            }
        }
    }

    public void deletePlayerLand(@NotNull Player player) {

        Land land = this.playerLands.remove(player);

        World world = player.getServer().getWorld(land.getWorld());
        if (world == null) return;

        Chunk chunk = world.getChunkAt(land.getX(), land.getZ());
        this.claimedChunks.remove(chunk);
        //TODO: Delete from database
    }

    public boolean isChunkClaimed(@NotNull Chunk chunk) {
        return this.claimedChunks.containsKey(chunk);
    }

    public void findConnectedChunk(@NotNull Player player, @NotNull Consumer<Land> consumer) {

        Land connectedChunk = null;
        Land result = null;

        ChunkRotation chunkRotation = ChunkRotation.getChunkRotation(player.getFacing().name());

        if (chunkRotation != null) {
            for (Chunk chunk : nearbyChunks(player.getChunk(), chunkRotation)) {

                Land worldChunk = getLand(chunk);
                if (worldChunk != null) {
                    if (worldChunk.getOwner().getUniqueId().equals(player.getUniqueId())) {
                        if (connectedChunk == null) {
                            connectedChunk = worldChunk;
                            continue;
                        }
                    }
                }

                if (connectedChunk != null) {

                    Land playerChunk = getPlayerLand(player.getUniqueId());

                    if (playerChunk != null) {
                        result = playerChunk;
                    } else {
                        result = connectedChunk;
                    }

                    break;
                }
            }
        }

        consumer.accept(result);
    }

    public boolean isChunkMerged(@NotNull Chunk chunk) {
        return false;
    }

    @Nullable
    public Land getLand(@NotNull Chunk chunk) {
        return this.claimedChunks.get(chunk);
    }

    public void merge(@NotNull Land base, @NotNull Chunk chunk) {
        base.getMergedChunks().add(ChunkUtil.getChunkIndex(chunk));
        updatePlayerLand(base);
    }

    public void updatePlayerLand(@NotNull Land land) {
        try (Session session = this.pandorasClusterApi.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(land);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            this.pandorasClusterApi.getLogger().log(Level.SEVERE, String.format("Cannot update worldchunk %s", land), e);
        }
    }

    @NotNull
    public Chunk[] nearbyChunks(@NotNull Chunk chunk, ChunkRotation... rotations) {

        Chunk[] chunks = new Chunk[rotations.length];

        for (int i = 0; i < rotations.length; i++) {
            chunks[i] = chunk.getWorld().getChunkAt(
                    chunk.getX() + rotations[i].getX(),
                    chunk.getZ() + rotations[i].getZ());
        }

        return chunks;
    }

    public boolean hasSameOwner(@NotNull Land land, @NotNull Land other) {
        return land.getOwner().compareTo(other.getOwner()) > 0;
    }
}

