package net.onelitefeather.pandorascluster.service;

import net.onelitefeather.pandorascluster.PandorasClusterPlugin;
import net.onelitefeather.pandorascluster.builder.LandBuilder;
import net.onelitefeather.pandorascluster.enums.ChunkRotation;
import org.bukkit.Chunk;
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

public class LandService1 {

    private final PandorasClusterPlugin pandorasClusterPlugin;
    private final List<WorldChunk> worldChunks;

    public LandService1(@NotNull PandorasClusterPlugin pandorasClusterPlugin) {
        this.pandorasClusterPlugin = pandorasClusterPlugin;
        this.worldChunks = new ArrayList<>();
        this.load(this.worldChunks::addAll);
    }

    @NotNull
    public List<WorldChunk> getWorldChunks() {
        return worldChunks;
    }

    public boolean isChunkClaimed(@NotNull Chunk chunk) {
        return getWorldChunk(chunk) != null;
    }

    public boolean isChunkMerged(@NotNull Chunk chunk) {

        boolean merged = false;
        List<WorldChunk> chunks = this.worldChunks;
        for (int i = 0; i < chunks.size() && !merged; i++) {
            WorldChunk worldChunk = chunks.get(i);
            if (worldChunk.getX() == chunk.getX() && worldChunk.getZ() == chunk.getZ()) {
                if (worldChunk.isMerged()) {
                    merged = true;
                }
            }
        }

        return merged;
    }

    @Nullable
    public WorldChunk getWorldChunk(@NotNull Chunk chunk) {
        for (WorldChunk current : getWorldChunks()) {

            for (WorldChunk merged : current.getMergedChunks()) {
                if (merged.getX() == chunk.getX() && merged.getZ() == chunk.getZ()) {
                    WorldChunk worldChunk = current.clone();
                    worldChunk.setX(merged.getX());
                    worldChunk.setZ(merged.getZ());
                    return worldChunk;
                }
            }

            if (current.getX() == chunk.getX() && current.getZ() == chunk.getZ()) {
                return current;
            }
        }
        return null;
    }

    public void load(Consumer<List<WorldChunk>> consumer) {

        List<WorldChunk> chunks = new ArrayList<>();

        try (Session session = this.pandorasClusterPlugin.getDatabaseService().getSessionFactory().openSession()) {
            session.beginTransaction();
            var query = session.createQuery("SELECT kd FROM WorldChunk kd", WorldChunk.class);
            chunks.addAll(query.list());
        } catch (HibernateException e) {
            this.pandorasClusterPlugin.getLogger().log(Level.SEVERE, "Could not load worldchunks.", e);
        }

        consumer.accept(chunks);
    }

    public void updatePlayerChunk(@NotNull WorldChunk worldChunk) {
        try (Session session = this.pandorasClusterPlugin.getDatabaseService().getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(worldChunk);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            this.pandorasClusterPlugin.getLogger().log(Level.SEVERE, String.format("Cannot update worldchunk %s", worldChunk.toString()), e);
        }
    }

    public void create(@NotNull WorldChunk worldChunk) {
        if (!this.getWorldChunks().contains(worldChunk)) {
            try (Session session = this.pandorasClusterPlugin.getDatabaseService().getSessionFactory().openSession()) {
                session.beginTransaction();
                session.persist(worldChunk);
                session.getTransaction().commit();
                this.getWorldChunks().add(worldChunk);
            } catch (HibernateException e) {
                this.pandorasClusterPlugin.getLogger().log(Level.SEVERE, String.format("Cannot update worldchunk %s", worldChunk.toString()), e);
            }
        }
    }

    public void findConnectedChunk(@NotNull Player player, @NotNull Consumer<WorldChunk> consumer) {

        WorldChunk connectedChunk = null;
        WorldChunk result = null;

        ChunkRotation chunkRotation = ChunkRotation.getChunkRotation(player.getFacing().name());

        if (chunkRotation != null) {
            for (Chunk chunk : nearbyChunks(player.getChunk(), chunkRotation)) {

                WorldChunk worldChunk = getWorldChunk(chunk);
                if (worldChunk != null) {
                    if (worldChunk.getOwner().equals(player.getUniqueId())) {
                        if (connectedChunk == null) {
                            connectedChunk = worldChunk;
                            continue;
                        }
                    }
                }

                if (connectedChunk != null) {

                    WorldChunk playerChunk = getPlayerChunk(player.getUniqueId());

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

    public boolean hasPlayerChunk(@NotNull UUID uuid) {
        return getPlayerChunk(uuid) != null;
    }

    @Nullable
    public WorldChunk getPlayerChunk(@NotNull UUID uuid) {

        WorldChunk worldChunk = null;
        for (int i = 0; i < this.worldChunks.size() && worldChunk == null; i++) {
            if (this.worldChunks.get(i).getOwner().equals(uuid)) {
                worldChunk = this.worldChunks.get(i);
            }
        }

        return worldChunk;
    }

    public void merge(@NotNull WorldChunk base, @NotNull Chunk chunk) {
        base.getMergedChunks().add(new LandBuilder(base).chunkX(chunk.getX()).chunkZ(chunk.getZ()).build());
        this.pandorasClusterPlugin.getWorldChunkManager().updatePlayerChunk(base);
    }

    public void unmerge(@NotNull Chunk base, @NotNull Chunk toUnMerge) {
        WorldChunk worldChunk = this.getWorldChunk(base);
        if (worldChunk != null) {

            WorldChunk chunkId = worldChunk.getMergedChunk(toUnMerge.getX(), toUnMerge.getZ());
            if (worldChunk.getMergedChunks().remove(chunkId)) {
                this.pandorasClusterPlugin.getWorldChunkManager().updatePlayerChunk(worldChunk);
            }
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
}
