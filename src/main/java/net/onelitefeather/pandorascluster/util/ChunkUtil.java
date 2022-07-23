package net.onelitefeather.pandorascluster.util;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://github.com/Minestom/Minestom/blob/master/src/main/java/net/minestom/server/utils/chunk/ChunkUtils.java">Util Class from Minestom</a>
 */
public class ChunkUtil {

    private ChunkUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Gets the chunk index of chunk coordinates.
     * <p>
     * Used when you want to store a chunk somewhere without using a reference to the whole object
     * (as this can lead to memory leaks).
     *
     * @param chunkX the chunk X
     * @param chunkZ the chunk Z
     * @return a number storing the chunk X and Z
     */
    public static long getChunkIndex(int chunkX, int chunkZ) {
        return (((long) chunkX) << 32) | (chunkZ & 0xffffffffL);
    }

    public static long getChunkIndex(@NotNull Chunk chunk) {
        return getChunkIndex(chunk.getX(), chunk.getZ());
    }

    /**
     * Converts a chunk index to its chunk X position.
     *
     * @param index the chunk index computed by {@link #getChunkIndex(int, int)}
     * @return the chunk X based on the index
     */
    public static int getChunkCoordX(long index) {
        return (int) (index >> 32);
    }

    /**
     * Converts a chunk index to its chunk Z position.
     *
     * @param index the chunk index computed by {@link #getChunkIndex(int, int)}
     * @return the chunk Z based on the index
     */
    public static int getChunkCoordZ(long index) {
        return (int) index;
    }

    @NotNull
    public static List<Player> getPlayersInChunk(@NotNull Chunk chunk) {

        List<Player> players = new ArrayList<>();
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Player player) {
                players.add(player);
            }
        }

        return players;
    }

}
